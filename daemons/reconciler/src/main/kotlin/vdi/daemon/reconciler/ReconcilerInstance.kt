package vdi.daemon.reconciler

import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.sql.select.TempHackCacheDBReconcilerTargetRecord
import vdi.component.kafka.router.KafkaRouter
import vdi.component.metrics.Metrics
import vdi.component.s3.DatasetDirectory
import vdi.component.s3.DatasetManager
import java.time.OffsetDateTime

/**
 * Component for synchronizing the dataset object store (the source of truth for
 * datasets) with a target database.
 *
 * This includes both our internal "cache DB", used to answer questions about
 * user dataset metadata/status and our application databases in which the
 * contents of datasets are installed.
 */
class ReconcilerInstance(
  private val targetDB: ReconcilerTarget,
  private val datasetManager: DatasetManager,
  private val kafkaRouter: KafkaRouter,
  private val slim: Boolean,
  private val deleteDryMode: Boolean = false
) {
  private val log = logger().delegate

  private var nextTargetDataset: VDIReconcilerTargetRecord? = null

  val name = targetDB.name

  suspend fun reconcile() {
    try {
      tryReconcile()

      // Initialize failures to zero.
      if (slim) {
        Metrics.Reconciler.Slim.failures.inc(0.0)
      } else {
        Metrics.Reconciler.Full.failedReconciliation.labels(targetDB.name).inc(0.0)
      }
    } catch (e: Exception) {
      // Don't re-throw error, ensure exception is logged and soldier on for
      // future reconciliation.
      log.error("failure running reconciler for " + targetDB.name, e)

      if (slim) {
        Metrics.Reconciler.Slim.failures.inc()
      } else {
        Metrics.Reconciler.Full.failedReconciliation.labels(targetDB.name).inc()
      }
    }
  }

  private suspend fun tryReconcile() {
    log.info("beginning reconciliation")

    targetDB.streamSortedSyncControlRecords().use { tryReconcile(datasetManager.streamAllDatasets().iterator(), it) }

    log.info("completed reconciliation")
  }

  private suspend fun tryReconcile(
    sourceIterator: Iterator<DatasetDirectory>,
    targetIterator: Iterator<VDIReconcilerTargetRecord>,
  ) {
    nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null

    // Iterate through datasets in S3.
    while (sourceIterator.hasNext()) {

      // Pop the next DatasetDirectory instance from the S3 stream.
      val sourceDatasetDir = sourceIterator.next()

      log.info("Checking dataset {}/{} for {}", sourceDatasetDir.ownerID, sourceDatasetDir.datasetID, targetDB.name)

      // Target stream is exhausted, everything left in source stream is missing from the target database!
      // Check again if target stream is exhausted, consume source stream if so.
      if (nextTargetDataset == null) {
        consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
        return
      }

      // Owner ID is included as part of sort, so it must be included when comparing streams.
      var comparableS3Id = "${sourceDatasetDir.ownerID}/${sourceDatasetDir.datasetID}"
      var comparableTargetId: String? = nextTargetDataset!!.getComparableID()

      // If target dataset stream is "ahead" of source stream, delete
      // the datasets from the target stream until we are aligned
      // again (or the target stream is consumed).
      if (comparableS3Id > comparableTargetId!!) {

        // Delete datasets until and advance target iterator until streams are aligned.
        while (nextTargetDataset != null && comparableS3Id.compareTo(comparableTargetId!!, false) > 0) {
          log.info("Attempting to delete dataset {} because {} is lexicographically greater than {}. Presumably {} is not in MinIO.", comparableTargetId, comparableS3Id, comparableTargetId, comparableTargetId)

          tryDeleteDataset(targetDB, nextTargetDataset!!)
          nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
          comparableTargetId = nextTargetDataset?.getComparableID()
        }
      }

      // Check again if target stream is exhausted, consume source stream if so.
      if (nextTargetDataset == null) {
        consumeEntireSourceStream(sourceIterator, sourceDatasetDir)
        return
      }

      // Owner ID is included as part of sort, so it must be included when comparing streams.
      comparableS3Id = "${sourceDatasetDir.ownerID}/${sourceDatasetDir.datasetID}"
      comparableTargetId = nextTargetDataset!!.getComparableID()

      if (comparableS3Id < comparableTargetId) {
        // Dataset is in source, but not in target. Send an event.
        if (!slim)
          Metrics.Reconciler.Full.missingInTarget.labels(targetDB.name).inc()

        sendSyncIfRelevant(sourceDatasetDir, SyncReason.MissingInTarget(targetDB))
      } else {

        // If dataset has a delete flag present and the dataset is not marked
        // as uninstalled from the target, then send a sync event.
        if (sourceDatasetDir.hasDeleteFlag() && !nextTargetDataset!!.isUninstalled) {
          sendSyncEvent(nextTargetDataset!!.ownerID, nextTargetDataset!!.datasetID, SyncReason.NeedsUninstall(targetDB))
        } else {
          val syncStatus = isOutOfSync(sourceDatasetDir, nextTargetDataset!!)

          // Dataset is in source and target. Check dates to see if sync is needed.
          if (syncStatus.isOutOfSync) {
            sendSyncIfRelevant(sourceDatasetDir, syncStatus.asSyncReason())
          }

          // Advance next target dataset pointer, we're done with this one
          nextTargetDataset = if (targetIterator.hasNext()) targetIterator.next() else null
        }
      }
    }

    // If we are doing a "slim" reconciliation, we just want to fire sync events
    // for things we think we may have missed, no deletes should be performed,
    // so we end here.  Full reconciliations will continue on to the deletion
    // calls.
    if (slim)
      return

    // If nextTargetDataset is not null at this point, then S3 was empty.
    nextTargetDataset?.also { tryDeleteDataset(targetDB, it) }

    // Consume target stream, deleting all remaining datasets.
    while (targetIterator.hasNext()) {
      tryDeleteDataset(targetDB, targetIterator.next())
    }
  }

  // FIXME: part of the temp hack to be removed when the target db deletes are
  //        moved to the hard-delete lane.
  private val tempYesterday = OffsetDateTime.now().minusDays(1)

  private suspend fun tryDeleteDataset(targetDB: ReconcilerTarget, record: VDIReconcilerTargetRecord) {
    log.info("attempting to delete {}/{}", record.ownerID, record.datasetID)

    // FIXME: temporary hack to be removed when target db deletes are moved to
    //        the hard-delete lane.
    if (targetDB.type == ReconcilerTargetType.Cache) {
      record as TempHackCacheDBReconcilerTargetRecord

      if (record.importStatus == DatasetImportStatus.Queued && record.inserted.isAfter(tempYesterday)) {
        log.info("skipping delete of dataset {}/{} as it is import-queued and less than 1 day old", record.ownerID, record.datasetID)
        return
      }
    }

    try {
      Metrics.Reconciler.Full.reconcilerDatasetDeleted.labels(targetDB.name).inc()
      if (!deleteDryMode) {
        log.info("Trying to delete dataset {}/{}", record.ownerID, record.datasetID)
        targetDB.deleteDataset(datasetID = record.datasetID, datasetType = record.type)
      } else {
        log.info("Would have deleted dataset {}/{}", record.ownerID, record.datasetID)
      }
    } catch (e: Exception) {
      // Swallow exception and alert if unable to delete. Reconciler can safely recover, but the dataset
      // may need a manual inspection.
      log.error("Failed to delete dataset ${record.ownerID}/${record.datasetID} of type ${record.type.name}:${record.type.version} from db ${targetDB.name}", e)
    }
  }

  /**
   * Returns true if any of our scopes are out of sync.
   */
  private fun isOutOfSync(ds: DatasetDirectory, targetLastUpdated: VDISyncControlRecord): SyncStatus {
    return SyncStatus(
      metaIsOOS = targetLastUpdated.metaUpdated.isBefore(ds.getMetaFile().lastModified()),
      sharesAreOOS = targetLastUpdated.sharesUpdated.isBefore(ds.getLatestShareTimestamp(targetLastUpdated.sharesUpdated)),
      installIsOOS = targetLastUpdated.dataUpdated.isBefore(ds.getInstallReadyTimestamp() ?: targetLastUpdated.dataUpdated),
    )
  }

  private fun sendSyncIfRelevant(sourceDatasetDir: DatasetDirectory, reason: SyncReason) {
    if (targetDB.type == ReconcilerTargetType.Install) {

      // Ensure the meta json file exists in S3 to protect against NPEs being
      // thrown on broken dataset directories.
      if (!sourceDatasetDir.hasMetaFile()) {
        log.warn("skipping dataset {}/{} as it has no meta json file", sourceDatasetDir.ownerID, sourceDatasetDir.datasetID)
        return
      }

      val relevantProjects = sourceDatasetDir.getMetaFile().load()!!.projects

      if (!relevantProjects.contains(targetDB.name)) {
        log.info("Skipping dataset {}/{} as it does not target {}", sourceDatasetDir.ownerID, sourceDatasetDir.datasetID, targetDB.name)
        return
      }
    }

    sendSyncEvent(sourceDatasetDir.ownerID, sourceDatasetDir.datasetID, reason)
  }

  private fun sendSyncEvent(ownerID: UserID, datasetID: DatasetID, reason: SyncReason) {
    log.info("sending reconciliation event for {}/{} for reason: {}", ownerID, datasetID, reason)
    kafkaRouter.sendReconciliationTrigger(ownerID, datasetID)

    if (slim)
      Metrics.Reconciler.Slim.datasetsSynced.inc()
    else
      Metrics.Reconciler.Full.reconcilerDatasetSynced.labels(targetDB.name).inc()
  }

  private fun consumeEntireSourceStream(
    sourceIterator: Iterator<DatasetDirectory>,
    sourceDatasetDir: DatasetDirectory
  ) {
    sendSyncIfRelevant(sourceDatasetDir, SyncReason.MissingInTarget(targetDB))
    while (sourceIterator.hasNext()) {
      sendSyncIfRelevant(sourceIterator.next(), SyncReason.MissingInTarget(targetDB))
    }
  }

  private inner class SyncStatus(val metaIsOOS: Boolean, val sharesAreOOS: Boolean, val installIsOOS: Boolean) {
    inline val isOutOfSync get() = metaIsOOS || sharesAreOOS || installIsOOS
    fun asSyncReason() =
      SyncReason.OutOfSync(metaIsOOS, sharesAreOOS, installIsOOS, targetDB)
  }

  private interface SyncReason {
    class OutOfSync(val meta: Boolean, val shares: Boolean, val install: Boolean, val target: ReconcilerTarget) : SyncReason {
      override fun toString() =
        "out of sync:" +
          (if (meta) " meta" else "") +
          (if (shares) " shares" else "") +
          (if (install) " install" else "") +
          " in target " + target.name
    }

    class MissingInTarget(val target: ReconcilerTarget) : SyncReason {
      override fun toString() = "missing from install target ${target.name}"
    }

    class NeedsUninstall(val target: ReconcilerTarget) : SyncReason {
      override fun toString() = "needs uninstallation from target ${target.name}"
    }
  }
}
