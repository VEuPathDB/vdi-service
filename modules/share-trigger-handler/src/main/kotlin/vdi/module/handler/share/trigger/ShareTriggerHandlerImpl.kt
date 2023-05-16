package vdi.module.handler.share.trigger

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.DatasetShare
import java.sql.SQLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.component.modules.VDIServiceModuleBase
import vdi.module.handler.share.trigger.config.ShareTriggerHandlerConfig

internal class ShareTriggerHandlerImpl(private val config: ShareTriggerHandlerConfig)
  : ShareTriggerHandler
  , VDIServiceModuleBase("share-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.shareTriggerTopic, config.kafkaConsumerConfig)
    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val wp = WorkerPool("share-workers", config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.shareTriggerMessageKey, ShareTrigger::class)
            .forEach { (userID, datasetID) ->
              log.debug("submitting job to share worker pool for user {}, dataset {}", userID, datasetID)
              wp.submit { executeJob(userID, datasetID, dm) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }

  private fun executeJob(userID: UserID, datasetID: DatasetID, dm: DatasetManager) {
    log.trace("executeJob(userID={}, datasetID={}, dm=...)", userID, datasetID)

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

        else {
          for (projectID in meta.projects) {
            AppDB.withTransaction(projectID) {
              it.updateSyncControlSharesTimestamp(datasetID, shareTimestamp)
              it.deleteDatasetVisibility(datasetID, recipientID)
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
