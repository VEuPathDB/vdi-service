package vdi.lane.prune.revision

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.withTransaction
import vdi.lib.kafka.EventMessage
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.s3.DatasetDirectory
import vdi.lib.s3.DatasetObjectStore
import java.util.concurrent.ConcurrentHashMap

internal class RevisionPruningTriggerHandlerImpl(private val config: RevisionPruningTriggerHandlerConfig, abortCB: (String?) -> Nothing)
  : RevisionPruningTriggerHandler
  , AbstractVDIModule("revision-pruning-trigger-handler", abortCB)
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  override suspend fun run() {
    val kc = requireKafkaConsumer(config.revisionPruningTopic, config.kafkaConsumerConfig)
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.revisionPruningMessageKey).forEach { processMessage(it, dm) }
        }
      }
    }

    log.info("closing kafka client")
    kc.close()
    log.info("kafka client closed")
    confirmShutdown()
  }

  private fun tryProcessMessage(message: EventMessage, dm: DatasetObjectStore) {
    if (datasetsInProgress.add(message.datasetID)) {
      try {
        processMessage(message, dm)
      } catch (e: Throwable) {
        log.error("{}/{}: unexpected error encountered while processing revision pruning event", message.userID, message.datasetID, e)
      }
    }
  }

  private fun processMessage(message: EventMessage, dm: DatasetObjectStore) {
    log.info("{}/{}: received revision pruning event from {}", message.userID, message.datasetID, message.eventSource)

    val s3Dir = dm.getDatasetDirectory(message.userID, message.datasetID)

    if (!s3Dir.exists()) {
      log.error("{}/{}: received revision pruning event but dataset directory does not exist", message.userID, message.datasetID)
      return
    }

    try {
      tryProcessDataset(message.userID, message.datasetID, s3Dir, dm)
    } catch (e: Throwable) {
      log.error("{}/{}: unexpected error encountered while processing revision pruning event ", message.userID, message.datasetID, e)
    }
  }

  private fun tryProcessDataset(userID: UserID, datasetID: DatasetID, s3Dir: DatasetDirectory, dm: DatasetObjectStore) {
    val meta = s3Dir.getMetaFile().load()

    if (meta == null) {
      log.error("{}/{}: no meta file could be loaded", userID, datasetID)
      return
    }

    if (meta.originalID == null) {
      if (meta.revisionHistory.isNotEmpty()) {
        log.error("{}/{}: broken dataset meta: metadata has revision history, but no original dataset ID", userID, datasetID)
      }

      log.warn("{}/{}: target has no revisions to prune", userID, datasetID)
      return
    }

    if (meta.revisionHistory.isEmpty()) {
      log.error("{}/{}: broken dataset meta: metadata has an original dataset ID but no revision history", userID, datasetID)
      return
    }

    val previousS3Dir = dm.getDatasetDirectory(userID, meta.getPreviousRevisionID())

    if (!previousS3Dir.exists()) {
      log.info("{}/{}: previous revision has already been purged from the object store", userID, datasetID)
      return
    }

    if (previousS3Dir.hasDeleteFlag()) {
      log.info("{}/{}: previous revision has already marked for uninstallation", userID, datasetID)
      return
    }

    processDataset(userID, s3Dir, previousS3Dir)
  }

  private fun processDataset(userID: UserID, curDir: DatasetDirectory, prevDir: DatasetDirectory) {
    copyShares(curDir, prevDir)

    CacheDB().withTransaction { db ->

    }
    // create link from previous revision to new revision in cache db
    // soft-delete old revision via the same mechanism used by the DELETE endpoint



  }

  private fun copyShares(curDir: DatasetDirectory, prevDir: DatasetDirectory) {
    for ((recipient, details) in prevDir.getShares()) {
      curDir.putShare(
        recipient,
        details.offer.load() ?: VDIDatasetShareOffer(VDIShareOfferAction.Grant),
        details.receipt.load() ?: VDIDatasetShareReceipt(VDIShareReceiptAction.Accept),
      )
    }
  }

  private fun VDIDatasetMeta.getPreviousRevisionID(): DatasetID {
    if (revisionHistory.size == 1)
      return originalID!!

    return revisionHistory[revisionHistory.lastIndex-1].revisionID
  }
}

