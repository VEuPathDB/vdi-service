package vdi.module.handler.share.trigger

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.common.util.isNull
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareOfferImpl
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareReceiptImpl
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.DatasetShare
import vdi.component.modules.VDIServiceModuleBase
import java.sql.SQLException
import java.time.OffsetDateTime

internal class ShareTriggerHandlerImpl(private val config: ShareTriggerHandlerConfig)
  : ShareTriggerHandler
  , VDIServiceModuleBase("share-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.shareTriggerTopic, config.kafkaConsumerConfig)
    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val wp = WorkerPool("share-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt())

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

    with(CacheDB.selectDataset(datasetID)) {
      if (isNull() || isDeleted)
        return
    }

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
      .forEach { (recipientID, share) -> processShare(datasetID, recipientID, meta.projects, share, shareTimestamp) }
  }

  private fun processShare(
    datasetID:      DatasetID,
    recipientID:    UserID,
    projects:       Iterable<ProjectID>,
    share:          DatasetShare,
    shareTimestamp: OffsetDateTime,
  ) {
    val offer   = share.offer.load()
    val receipt = share.receipt.load()

    // There is no offer object in S3.  As it may have previously existed
    // and been deleted, we will go through and make sure we clear out any
    // possible records that would allow the recipient user to still see the
    // dataset.
    if (offer == null) {
      cleanupAppDBs(datasetID, recipientID, projects, shareTimestamp)

      // Remove the offer record from the cache db.
      CacheDB.withTransaction {
        it.deleteShareOffer(datasetID, recipientID)
      }
    } else {
      CacheDB.withTransaction {
        it.upsertDatasetShareOffer(DatasetShareOfferImpl(datasetID, recipientID, offer.action))
      }
    }

    // There is no receipt object in S3.  As it may have previously existed
    // and been deleted, we will go through and make sure we clear out any
    // possible records that would allow the recipient user to still see the
    // dataset.
    if (receipt == null) {
      cleanupAppDBs(datasetID, recipientID, projects, shareTimestamp)

      // Remove the receipt record from the cache DB.
      CacheDB.withTransaction {
        it.deleteShareReceipt(datasetID, recipientID)
      }
    } else {
      CacheDB.withTransaction {
        it.upsertDatasetShareReceipt(DatasetShareReceiptImpl(datasetID, recipientID, receipt.action))
      }
    }

    // If we have both the offer and receipt objects in S3, then we can
    // check the share status and decide whether the dataset should be
    // visible for the recipient user.
    if (offer != null && receipt != null) {
      if (offer.action == VDIShareOfferAction.Grant && receipt.action == VDIShareReceiptAction.Accept) {
        for (projectID in projects) {
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
      } else {
        cleanupAppDBs(datasetID, recipientID, projects, shareTimestamp)
      }
    }
  }

  private fun cleanupAppDBs(
    datasetID: DatasetID,
    recipientID: UserID,
    projects: Iterable<ProjectID>,
    shareTimestamp: OffsetDateTime
  ) {
    for (project in projects) {
      AppDB.withTransaction(project) {
        it.updateSyncControlSharesTimestamp(datasetID, shareTimestamp)
        it.deleteDatasetVisibility(datasetID, recipientID)
      }
    }
  }
}
