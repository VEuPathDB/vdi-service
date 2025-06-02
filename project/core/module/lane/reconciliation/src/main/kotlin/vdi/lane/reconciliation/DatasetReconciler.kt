package vdi.lane.reconciliation

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.lane.reconciliation.util.*
import vdi.core.db.app.AppDB
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.withTransaction
import vdi.lib.kafka.EventSource
import vdi.lib.kafka.router.KafkaRouter
import vdi.core.logging.markedLogger
import vdi.core.metrics.Metrics
import vdi.lib.s3.DatasetObjectStore

internal class DatasetReconciler(
  private val cacheDB: CacheDB = CacheDB(),
  private val appDB: AppDB = AppDB(),
  private val eventRouter: KafkaRouter,
  private val datasetManager: DatasetObjectStore,
) {
  // TODO: If the import-ready file is newer than the install-ready or manifest
  //       files then we should fire an import event.
  // TODO: If the raw upload file is newer than the import-ready file then we
  //       should fire an upload processing event.

  fun reconcile(userID: UserID, datasetID: DatasetID, source: EventSource) {
    val logger = markedLogger(userID, datasetID)
    logger.info("beginning reconciliation")
    try {
      reconcile(ReconciliationContext(datasetManager.getDatasetDirectory(userID, datasetID), source, logger))
      logger.info("reconciliation completed")
    } catch (e: CriticalReconciliationError) {
      logger.error("reconciliation failed")
    } catch (e: Throwable) {
      Metrics.ReconciliationHandler.errors.inc()
      logger.error("reconciliation failed", e)
    }
  }

  private fun reconcile(ctx: ReconciliationContext) {
    val reimport = shouldReimport(ctx)

    // Make sure the cache db has at least the base records for this dataset.
    cacheDB.tryInitDataset(ctx, calcNewStatus(ctx, reimport))

    // If the dataset has been marked as deleted, or if there is a revision flag
    // ensure that it has been uninstalled.
    if (ctx.hasDeleteFlag || ctx.hasRevisedFlag)
      return ensureUninstalled(ctx)

    // The upload file should only exist before an attempt has been made to
    // process the raw upload into an import-ready form.
    if (ctx.hasRawUpload)
      handleHasUpload(ctx)

    // If we determined that a reimport is possible and required (the
    // import-ready.zip file exists but no install-ready.zip file exists) then
    // perform the reimport and halt here.
    if (reimport == ReimportIndicator.NeedReimport)
      return tryReimport(ctx)

    // If we have no import-ready file, then something is wrong, but we may have
    // install-ready files we can process?
    if (!ctx.hasImportReadyData)
      ctx.logger.error("missing import-ready file")

    // If we determined that a reimport is not needed by the existence and age
    // of the install-ready files, ensure that the import status for the dataset
    // is correct.
    if (reimport == ReimportIndicator.ReimportNotNeeded)
      if (cacheDB.getCacheImportControl(ctx) == DatasetImportStatus.Queued)
        cacheDB.updateImportStatus(ctx, DatasetImportStatus.Complete)

    if (reimport != ReimportIndicator.ReimportNotPossible) {
      if (!ctx.hasInstallReadyData)
        ctx.logger.warn("missing install-ready file")

      if (!ctx.hasManifest)
        ctx.logger.warn("missing manifest file")
    }

    runSync(ctx)

    if (!ctx.haveFiredAnyEvents)
      ctx.logger.info("no events fired")
  }

  private fun calcNewStatus(ctx: ReconciliationContext, oldStatus: ReimportIndicator) =
    when (oldStatus) {
      ReimportIndicator.ReimportNotNeeded -> DatasetImportStatus.Complete
      ReimportIndicator.NeedReimport -> DatasetImportStatus.Queued
      ReimportIndicator.ReimportNotPossible -> {
        if (ctx.hasInstallReadyData && ctx.hasManifest)
          DatasetImportStatus.Complete
        else
          DatasetImportStatus.Failed
      }
    }

  private fun ensureUninstalled(ctx: ReconciliationContext) {
    val dataset = cacheDB.requireCacheDatasetRecord(ctx)

    if (!dataset.isDeleted) {
      ctx.safeExec("failed to mark dataset as deleted in cache db") {
        cacheDB.withTransaction { it.updateDatasetDeleted(ctx.datasetID, true) }
      }
    }

    if (!appDB.isFullyUninstalled(ctx))
      fireUninstallEvent(ctx)
  }

  private fun handleHasUpload(ctx: ReconciliationContext) {
    // If an import-ready.zip file exists for the dataset then we can delete the
    // raw upload file and ensure that the upload control table in the cache db
    // indicates that the upload was successfully processed.
    if (ctx.hasImportReadyData) {
      ctx.safeExec("failed to delete raw upload file") { ctx.datasetDirectory.deleteUploadFile() }
      // TODO: Write completed status to upload control table in cache db
    }

    // If no import-ready.zip file exists for the dataset, then check the cache
    // db upload control table.  If the control table indicates that the upload
    // processing failed then delete the raw upload file.  If the control table
    // indicates that the upload processing is queued or in progress then fire
    // an upload event to make sure the upload is processed.  Best case, the
    // upload is already being processed and the upload handler will reject the
    // job, worst case we caught an interrupted upload job.
    else {
      // TODO: Implement with robust async upload
    }
  }

  private fun runSync(ctx: ReconciliationContext) {
    val syncStatus = checkSyncStatus(ctx)

    if (syncStatus.metaOutOfSync)
      fireUpdateMetaEvent(ctx)

    if (syncStatus.sharesOutOfSync)
      fireShareEvent(ctx)

    // If we've already fired an import event, then an install event will be
    // triggered by that import, meaning there is no need to fire an install
    // event here.
    if (!ctx.haveFiredImportEvent && syncStatus.installOutOfSync)
      fireInstallEvent(ctx)
    else if (!syncStatus.installOutOfSync && ctx.meta.originalID != null && isInstalled(ctx))
      ensureRevisionFlags(ctx)
  }

  private fun checkSyncStatus(ctx: ReconciliationContext): SyncIndicator {
    val cacheDBSyncControl = cacheDB.requireSyncControl(ctx)

    val metaTimestamp = ctx.datasetDirectory.getMetaTimestamp()
      ?: cacheDBSyncControl.metaUpdated

    val installDataTimestamp = ctx.datasetDirectory.getInstallReadyTimestamp()
      ?: cacheDBSyncControl.dataUpdated

    val latestShareTimestamp = ctx.datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)

    val indicator = SyncIndicator(
      metaOutOfSync = cacheDBSyncControl.metaUpdated.isBefore(metaTimestamp),
      sharesOutOfSync = cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp),
      installOutOfSync = cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp),
    )

    if (indicator.fullyOutOfSync)
      return indicator

    ctx.meta.installTargets.forEach { projectID ->
      val appDB = appDB.accessor(projectID, ctx.meta.type)

      if (appDB == null) {
        ctx.logger.warn("skipping dataset state comparison for disabled target {}", projectID)
        return@forEach
      }

      val sync = appDB.selectSyncControl(ctx)
        // If the dataset sync record does not exist at all, then fire a sync
        // action for shares and install.  The share sync is most likely going
        // to be processed before the install, so the shares probably won't make
        // it to the install target until the next big reconciler run.
        ?: return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)

      if (!indicator.metaOutOfSync && sync.metaUpdated.isBefore(metaTimestamp))
        indicator.metaOutOfSync = true

      if (!indicator.sharesOutOfSync && sync.sharesUpdated.isBefore(latestShareTimestamp))
        indicator.sharesOutOfSync = true

      if (!indicator.installOutOfSync && sync.dataUpdated.isBefore(installDataTimestamp))
        indicator.installOutOfSync = true

      if (indicator.fullyOutOfSync)
        return indicator
    }

    return indicator
  }

  private fun tryReimport(ctx: ReconciliationContext) {
    cacheDB.updateImportStatus(ctx, DatasetImportStatus.Queued)
    cacheDB.dropImportMessages(ctx)
    fireImportEvent(ctx)
  }

  private fun isInstalled(ctx: ReconciliationContext) =
    ctx.meta.installTargets.all {
      (AppDB().accessor(it, ctx.meta.type) ?: return@all true)
        .selectDatasetInstallMessage(ctx.datasetID, InstallType.Data)?.status == InstallStatus.Complete
    }

  private fun ensureRevisionFlags(ctx: ReconciliationContext) {
    val targets = ArrayList<DatasetID>(ctx.meta.revisionHistory.size)
    targets.add(ctx.meta.originalID!!)
    ctx.meta.revisionHistory.asSequence()
      .sortedByDescending { it.timestamp }
      .drop(1) // don't mark latest/current revision as an old revision
      .forEach { targets.add(it.revisionID) }

    targets.forEach {
      datasetManager.getDatasetDirectory(ctx.userID, it)
        .getRevisedFlag()
        .also { flag -> if (!flag.exists()) flag.touch() }
    }
  }

  /**
   * Dataset reimport is needed in the following cases:
   *
   * 1. Install-ready and/or manifest files are missing AND import has not
   *    already failed
   * 2. Import has failed but there are no import messages.
   * 3. TODO: import-ready file is newer than installable and/or manifest files
   */
  private fun shouldReimport(ctx: ReconciliationContext) =
    when {
      // If we don't have an import-ready file, we can't rerun the import
      // process
      !ctx.hasImportReadyData -> ReimportIndicator.ReimportNotPossible

      // If the import was found to be invalid, then it's a data problem and no
      // amount of reimport attempts will help.
      cacheDB.isImportInvalid(ctx) -> ReimportIndicator.ReimportNotPossible

      // If we failed the last import attempt, then only rerun the import if we
      // have no import messages so that we can repopulate that table to
      // indicate to the user what happened.
      cacheDB.isImportFailed(ctx) -> if (cacheDB.isMissingImportMessage(ctx)) {
        ctx.logger.warn("import failed, but we have no error message; marking dataset as needing a reimport to populate the error message")
        ReimportIndicator.NeedReimport
      } else {
        ctx.logger.info("import failed, reimport attempt not necessary")
        ReimportIndicator.ReimportNotNeeded
      }

      // If we are missing the install-ready file and/or the manifest file then
      // we need to rerun the import to get those files back.
      !(ctx.hasInstallReadyData && ctx.hasManifest) -> ReimportIndicator.NeedReimport

      // We have an import-ready file, we have no failed import record, we have
      // both the install-ready and manifest files.  There is no need to rerun
      // the import process.
      else -> ReimportIndicator.ReimportNotNeeded
    }

  // region Kafka

  private fun fireImportEvent(ctx: ReconciliationContext) {
    ctx.logger.info("firing import event")
    ctx.safeExec("failed to fire import trigger") {
      eventRouter.sendImportTrigger(ctx.userID, ctx.datasetID, ctx.source)
      ctx.haveFiredImportEvent = true
    }
  }

  private fun fireUpdateMetaEvent(ctx: ReconciliationContext) {
    ctx.logger.info("firing update meta event")
    ctx.safeExec("failed to fire update-meta trigger") {
      eventRouter.sendUpdateMetaTrigger(ctx.userID, ctx.datasetID, ctx.source)
      ctx.haveFiredMetaUpdateEvent = true
    }
  }

  private fun fireShareEvent(ctx: ReconciliationContext) {
    ctx.logger.info("firing share event")
    ctx.safeExec("failed to send share trigger") {
      eventRouter.sendShareTrigger(ctx.userID, ctx.datasetID, ctx.source)
      ctx.haveFiredShareEvent = true
    }
  }

  private fun fireInstallEvent(ctx: ReconciliationContext) {
    ctx.logger.info("firing data install event")
    ctx.safeExec("failed to send install-data trigger") {
      eventRouter.sendInstallTrigger(ctx.userID, ctx.datasetID, ctx.source)
      ctx.haveFiredInstallEvent = true
    }
  }

  private fun fireUninstallEvent(ctx: ReconciliationContext) {
    ctx.logger.info("firing soft-delete event")
    ctx.safeExec("failed to send soft-delete trigger") {
      eventRouter.sendSoftDeleteTrigger(ctx.userID, ctx.datasetID, ctx.source)
      ctx.haveFiredUninstallEvent = true
    }
  }

  // endregion Kafka
}
