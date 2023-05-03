package vdi.module.handler.share.trigger

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.DatasetShare
import java.sql.SQLException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.module.handler.share.trigger.config.ShareTriggerHandlerConfig

internal class ShareTriggerHandlerImpl(private val config: ShareTriggerHandlerConfig) : ShareTriggerHandler {
  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  override suspend fun start() {
    if (!started) {
      log.info("starting share-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering share-trigger-handler module shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val kc = requireKafkaConsumer()
    val s3 = requireS3Bucket(requireS3Client())
    val dm = DatasetManager(s3)
    val wp = WorkerPool("share-workers", config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      launch {
        while (!shutdownTrigger.isTriggered()) {
          kc.selectShareTriggers()
            .forEach { (userID, datasetID) -> wp.submit { executeJob(userID, datasetID, dm) } }
        }

        wp.stop()
      }

      wp.start()
    }

    shutdownConfirm.trigger()
  }

  private suspend inline fun <T> safeExec(err: String, fn: () -> T): T =
    try {
      fn()
    } catch (e: Throwable) {
      log.error(err, e)
      stop()
      throw e
    }

  private suspend fun requireKafkaConsumer() = safeExec("failed to create KafkaConsumer instance") {
    KafkaConsumer(config.shareTriggerTopic, config.kafkaConsumerConfig)
  }

  private suspend fun requireS3Client() = safeExec("failed to create S3 client instance") {
    S3Api.newClient(config.s3Config)
  }

  private suspend fun requireS3Bucket(s3: S3Client) = safeExec("failed to lookup target S3 bucket") {
    s3.buckets[config.s3Bucket] ?: throw IllegalStateException("bucket ${config.s3Bucket} does not exist!")
  }

  private fun KafkaConsumer.selectShareTriggers() =
    receive()
      .asSequence()
      .filter { it.key == config.shareTriggerMessageKey }
      .map {
        try {
          JSON.readValue<ShareTrigger>(it.value)
        } catch (e: Throwable) {
          log.warn("received an invalid message body from Kafka: {}", it)
          null
        }
      }
      .filterNotNull()

  private fun executeJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    log.trace("executeJob(userID={}, datasetID={}, dm=...", userID, datasetID)

    log.debug("looking up dataset directory for user {}, dataset {}", userID, datasetID)
    val dir = dm.getDatasetDirectory(userID, datasetID)

    val syncControl = CacheDB.selectSyncControl(datasetID)

    if (syncControl == null) {
      log.debug("skipping share event for dataset {} (user {}): dataset does not yet have a sync control record", datasetID, userID)
      return
    }

    val shareTimestamp = dir.getLatestShareTimestamp(syncControl.sharesUpdated)

    if (!shareTimestamp.isAfter(syncControl.sharesUpdated)) {
      log.debug("skipping share event for dataset {} (user {}): already up to date", datasetID, userID)
      return
    }

    CacheDB.withTransaction { it.updateShareSyncControl(datasetID, shareTimestamp) }

    if (!dir.isImportComplete()) {
      log.debug("skipping share event for dataset {} (user {}): dataset is not import complete", datasetID, userID)
      return
    }

    val meta = dir.getMeta().load()!!

    dir.getShares()
      .forEach { (recipientID, shares) ->

        if (shares.isVisible()) {
          for (projectID in meta.projects) {
            AppDB.withTransaction(projectID) {
              it.updateSyncControlSharesTimestamp(datasetID, shareTimestamp)
              it.deleteDatasetVisibility(datasetID, recipientID)
            }
          }
        }

        else {
          for (projectID in meta.projects) {
            AppDB.withTransaction(projectID) {
              it.updateSyncControlSharesTimestamp(datasetID, shareTimestamp)
              try {
                it.insertDatasetVisibility(datasetID, recipientID)
              } catch (e: SQLException) {
                // swallow unique constraint violations
                if (e.errorCode != 1) {
                  throw e
                }
              }
            }
          }
        }
      }
  }

  private fun DatasetShare.isVisible(): Boolean {
    val offer   = offer.load() ?: return false
    val receipt = receipt.load() ?: return false

    if (offer.action != VDIShareOfferAction.Grant)
      return false

    if (receipt.action != VDIShareReceiptAction.Accept)
      return false

    return true
  }
}