package vdi.core.reconciler

import java.time.OffsetDateTime
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.TempHackCacheDBReconcilerTargetRecord
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.db.model.SyncControlRecord
import vdi.core.kafka.EventSource
import vdi.core.kafka.router.KafkaRouter
import vdi.core.metrics.Metrics
import vdi.core.s3.*
import vdi.logging.createLoggerMark
import vdi.logging.mark
import vdi.logging.markedLogger
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.util.events.EventIDs

/**
 * Component for synchronizing the dataset object store (the source of truth for
 * datasets) with a target database.
 *
 * This includes both our internal "cache DB", used to answer questions about
 * user dataset metadata/status and our application databases in which the
 * contents of datasets are installed.
 */
internal class ReconcilerInstance(
  private val targetDB: ReconcilerTarget,
  private val datasetManager: DatasetObjectStore,
  private val kafkaRouter: KafkaRouter,
  private val slim: Boolean,
  private val deletesEnabled: Boolean
) {
  private val log = markedLogger(installTarget = targetDB.name)

  private var nextInstallTargetDataset: ReconcilerTargetRecord? = null

  val name = targetDB.name

  internal suspend fun reconcile() {
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
    targetDB.streamSortedSyncControlRecords().use { tryReconcile(datasetManager.streamAllDatasets(), it) }
  }

  private suspend fun tryReconcile(
    objectStoreIterator:   Iterator<DatasetDirectory>,
    installTargetIterator: Iterator<ReconcilerTargetRecord>,
  ) {
    log.trace("tryReconcile(...)")
    nextInstallTargetDataset = installTargetIterator.nextOrNull()

    // Iterate through datasets in S3.
    while (objectStoreIterator.hasNext()) {
      // Pop the next DatasetDirectory instance from the S3 stream.
      val sourceDatasetDir = objectStoreIterator.next()

      // If target stream is exhausted, everything left in the source stream is
      // absent from the target database.
      if (nextInstallTargetDataset == null) {
        objectStoreIterator.consumeAndTrySync(sourceDatasetDir)
        return
      }

      // Owner ID is included as part of sort, so it must be included when comparing streams.
      val comparableS3Id = sourceDatasetDir.getComparableID()
      var comparableTargetId: String? = nextInstallTargetDataset!!.getComparableID()

      // If install-target dataset stream is "ahead" of source stream, uninstall
      // the datasets from the target database until either the streams are
      // aligned again, or the install-target dataset stream is consumed.
      if (comparableS3Id > comparableTargetId!!)
        installTargetIterator.uninstallUntilAligned(comparableS3Id, comparableTargetId)

      // Check again if target stream is exhausted, consume source stream if so.
      if (nextInstallTargetDataset == null) {
        objectStoreIterator.consumeAndTrySync(sourceDatasetDir)
        return
      }

      // Owner ID is included as part of sort, so it must be included when comparing streams.
      comparableTargetId = nextInstallTargetDataset!!.getComparableID()

      // If the object-store dataset ID is less than the ID we got from the
      // target DB
      if (comparableS3Id < comparableTargetId) {
        // If the dataset is in the object store, but not in the install-target,
        // send an event.
        if (sendSyncIfRelevant(sourceDatasetDir, SyncReason.MissingInTarget(targetDB)) && !slim)
          Metrics.Reconciler.Full.missingInTarget.labels(targetDB.name).inc()
      } else {
        // If the dataset should be uninstalled due to either having a
        // soft-delete or revision flag
        if (sourceDatasetDir.hasDeleteFlag() || sourceDatasetDir.hasRevisedFlag()) {
          nextInstallTargetDataset!!.ensureUninstalled()
        } else {
          sourceDatasetDir.ensureSynchronized(comparableS3Id)

          // Advance next target dataset pointer, we're done with this one
          nextInstallTargetDataset = installTargetIterator.nextOrNull()
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
    nextInstallTargetDataset?.also { tryUninstallDataset(targetDB, it) }

    // Consume target stream, deleting all remaining datasets.
    while (installTargetIterator.hasNext()) {
      tryUninstallDataset(targetDB, installTargetIterator.next())
    }
  }

  private suspend fun Iterator<ReconcilerTargetRecord>.uninstallUntilAligned(
    comparableSourceId: String,
    startingTargetId: String,
  ) {
    var comparableTargetId: String? = startingTargetId

    // Uninstall datasets and advance target database iterator until the streams
    // are aligned.
    while (nextInstallTargetDataset != null && comparableSourceId.compareTo(comparableTargetId!!, false) > 0) {
      if (!slim) {
        log.info(
          "next install-target dataset {} has a lexicographically lesser ID than the next source dataset ({})",
          comparableTargetId.trimSortSuffix(),
          comparableSourceId.trimSortSuffix(),
        )

        tryUninstallDataset(targetDB, nextInstallTargetDataset!!)
      }

      nextInstallTargetDataset = nextOrNull()
      comparableTargetId = nextInstallTargetDataset?.getComparableID()
    }
  }

  private suspend fun ReconcilerTargetRecord.ensureUninstalled() {
    // If the dataset has not been marked as uninstalled in this target database
    if (!isUninstalled)
      // then fire a sync event
      sendSyncEvent(ownerID, datasetID, SyncReason.NeedsUninstall(targetDB))
  }

  private suspend fun DatasetDirectory.ensureSynchronized(comparableS3Id: String) {
    // The dataset does not have a delete flag present in MinIO

    // If for some reason it is marked as uninstalled in the target
    // database, log an error and skip
    if (nextInstallTargetDataset!!.isUninstalled) {
      log.error(
        "dataset {} is marked as uninstalled in target {} but has no deletion flag present in MinIO",
        comparableS3Id.trimSortSuffix(),
        targetDB.name
      )
      return
    }

    val syncStatus = isOutOfSync(nextInstallTargetDataset!!)

    // Dataset is in source and target. Check dates to see if sync is needed.
    if (syncStatus.isOutOfSync) {
      sendSyncIfRelevant(this, syncStatus.toSyncReason(targetDB))
    }
  }

  // FIXME: part of the temp hack to be removed when the target db deletes are
  //        moved to the hard-delete lane.
  private val tempYesterday = OffsetDateTime.now().minusDays(1)

  private suspend fun tryUninstallDataset(targetDB: ReconcilerTarget, record: ReconcilerTargetRecord) {
    val eventID = EventIDs.issueID()

    val log = log.mark(eventID, record.ownerID, record.datasetID)

    log.info("attempting to delete dataset")

    // FIXME: temporary hack to be removed when target db deletes are moved to
    //        the hard-delete lane.
    if (targetDB.type == ReconcilerTargetType.Cache) {
      record as TempHackCacheDBReconcilerTargetRecord

      if (record.importStatus == DatasetImportStatus.Queued && record.inserted.isAfter(tempYesterday)) {
        log.info("skipping dataset delete; it is import-queued and less than 1 day old")
        return
      }
    }

    try {
      Metrics.Reconciler.Full.reconcilerDatasetDeleted.labels(targetDB.name).inc()
      if (deletesEnabled) {
        log.info("deleting dataset")
        targetDB.deleteDataset(eventID, record)
      } else {
        log.info("would have deleted dataset")
      }
    } catch (e: Exception) {
      // Swallow exception and alert if unable to delete. Reconciler can safely recover, but the dataset
      // may need a manual inspection.
      log.error("failed to delete dataset", e)
    }
  }

  /**
   * Calls [sendSyncEvent] if the dataset represented by [sourceDatasetDir] is
   * relevant to the current target application DB.
   *
   * @param sourceDatasetDir MinIO dataset directory representation for the
   * dataset in question.
   *
   * @param reason Reason a sync event should be fired.
   *
   * @return A boolean value indicating whether the dataset was relevant to the
   * current target project and a reconciliation event was fired.
   */
  private suspend fun sendSyncIfRelevant(sourceDatasetDir: DatasetDirectory, reason: SyncReason): Boolean {
    if (targetDB.type == ReconcilerTargetType.Install) {

      // Ensure the meta JSON file exists in S3 to protect against NPEs being
      // thrown on broken dataset directories.
      if (!sourceDatasetDir.hasMetaFile()) {
        log.warn("skipping dataset {}/{} as it has no meta json file", sourceDatasetDir.ownerID, sourceDatasetDir.datasetID)
        return false
      }

      val relevantProjects = sourceDatasetDir.getMetaFile().load()!!.installTargets

      if (!relevantProjects.contains(targetDB.name)) {
        return false
      }
    }

    sendSyncEvent(sourceDatasetDir.ownerID, sourceDatasetDir.datasetID, reason)
    return true
  }

  private suspend fun sendSyncEvent(ownerID: UserID, datasetID: DatasetID, reason: SyncReason) {
    val eventID = EventIDs.issueID()

    log.info("{} sending reconciliation event: {}", createLoggerMark(eventID, ownerID, datasetID), reason)

    kafkaRouter.sendReconciliationTrigger(
      eventID,
      ownerID,
      datasetID,
      if (slim) EventSource.SlimReconciler else EventSource.FullReconciler,
    )

    if (slim)
      Metrics.Reconciler.Slim.datasetsSynced.inc()
    else
      Metrics.Reconciler.Full.reconcilerDatasetSynced.labels(targetDB.name).inc()
  }

  private suspend fun Iterator<DatasetDirectory>.consumeAndTrySync(first: DatasetDirectory) {
    sendSyncIfRelevant(first, SyncReason.MissingInTarget(targetDB))

    while (hasNext())
      sendSyncIfRelevant(next(), SyncReason.MissingInTarget(targetDB))
  }

  /**
   * Returns true if any of our scopes are out of sync.
   */
  private fun DatasetDirectory.isOutOfSync(targetLastUpdated: SyncControlRecord): SyncStatus {
    return SyncStatus(
      metaOutOfSync    = targetLastUpdated.metaUpdated.isBefore(getMetaFile().lastModified())
        || getLatestVariablePropertiesTimestamp()?.let { targetLastUpdated.metaUpdated.isBefore(it) } == true,
      sharesOutOfSync  = targetLastUpdated.sharesUpdated.isBefore(getLatestShareTimestamp(targetLastUpdated.sharesUpdated)),
      installOutOfSync = targetLastUpdated.dataUpdated.isBefore(getInstallReadyTimestamp() ?: targetLastUpdated.dataUpdated),
    )
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline fun <T> Iterator<T>.nextOrNull() =
    if (hasNext()) next() else null

  private fun ReconcilerTargetRecord.getComparableID() = "$ownerID/$datasetID".appendSortSuffix()

  private fun DatasetDirectory.getComparableID() = "$ownerID/$datasetID".appendSortSuffix()

  /**
   * Appends `.z` to the dataset ID value to make the sorting predictable when
   * comparing against datasets with revision suffixes.
   */
  private fun String.appendSortSuffix() = if (contains('.')) this else "$this.z"

  /**
   * Removes the sort suffix value `.z` from the dataset ID value.
   */
  private fun String.trimSortSuffix() = substringBefore(".z")
}
