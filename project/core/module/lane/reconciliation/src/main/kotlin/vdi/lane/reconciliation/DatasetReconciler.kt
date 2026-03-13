package vdi.lane.reconciliation

import vdi.core.db.app.AppDB
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.kafka.router.KafkaRouter
import vdi.core.metrics.Metrics
import vdi.core.s3.*
import vdi.lane.reconciliation.util.*
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetID
import vdi.model.meta.toDatasetID

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

  fun reconcile(ctx: ReconcilerContext) {
    ctx.logger.info("beginning reconciliation")
    try {
      reconcile(ReconcilerTarget(
        ctx.datasetId,
        datasetManager.getDatasetDirectory(ctx.ownerId, DatasetID(ctx.datasetId)),
        ctx.source,
        ctx.logger,
      ))
      ctx.logger.info("reconciliation completed")
    } catch (_: CriticalReconciliationError) {
      ctx.logger.error("reconciliation failed")
    } catch (e: Throwable) {
      Metrics.ReconciliationHandler.errors.inc()
      ctx.logger.error("reconciliation failed", e)
    }
  }

  private fun reconcile(ctx: ReconcilerTarget) {
    val reimport = shouldReimport(ctx)

    // Make sure the cache db has at least the base records for this dataset.
    cacheDB.tryInitDataset(ctx, calcNewStatus(ctx, reimport))

    // If, by race condition, both a revised flag and delete flag were created,
    // remove the delete flag.
    if (ctx.hasDeleteFlag() && ctx.hasRevisedFlag())
      ctx.datasetDirectory.deleteDeleteFlag()

    // If the dataset has been marked as deleted, or if there is a revision flag
    // ensure that it has been uninstalled.
    if (ctx.hasDeleteFlag() || ctx.hasRevisedFlag())
      return ensureUninstalled(ctx)

    // The upload file should only exist before an attempt has been made to
    // process the raw upload into an import-ready form.
    //
    // THIS IS NOT YET FULLY IMPLEMENTED!!  THE IMPORT READY FILE _IS_ THE RAW
    // UPLOAD UNTIL WE FIX THE ASYNC UPLOAD PROCESS
    if (ctx.hasRawUpload())
      handleHasUpload(ctx)

    // If we determined that a reimport is possible and required (the
    // import-ready.zip file exists but no install-ready.zip file exists) then
    // perform the reimport and halt here.
    if (reimport == ReimportIndicator.NEEDS_REIMPORT) {
      return tryReimport(ctx)
    }

    // If we have no import-ready file then the upload probably failed.
    if (!ctx.hasImportReadyData()) {
      // Check for an upload error log
      if (ctx.hasUploadError()) {
        return synchronizeUploadStatus(ctx, DatasetUploadStatus.Failed)
      }

      ctx.logger.error("missing import-ready file and no upload error was recorded")
    } else {
      // if we do have an import ready file, ensure the cache db knows that the
      // upload succeeded
      synchronizeUploadStatus(ctx, DatasetUploadStatus.Success)
    }

    // If we determined that a reimport is not needed by the existence and age
    // of the install-ready files, ensure that the import status for the dataset
    // is correct.
    if (reimport == ReimportIndicator.REIMPORT_NOT_NECESSARY)
      if (cacheDB.getCacheImportControl(ctx) == DatasetImportStatus.Queued)
        cacheDB.updateImportStatus(ctx, DatasetImportStatus.Complete)

    if (reimport != ReimportIndicator.REIMPORT_NOT_POSSIBLE) {
      if (!ctx.hasInstallReadyData())
        ctx.logger.warn("missing install-ready file")

      if (!ctx.hasManifest())
        ctx.logger.warn("missing manifest file")
    }

    runDatasetSync(ctx)

    if (!ctx.haveFiredAnyEvents())
      ctx.logger.info("no events fired")
  }

  private fun calcNewStatus(ctx: ReconcilerTarget, oldStatus: ReimportIndicator) =
    when (oldStatus) {
      ReimportIndicator.REIMPORT_NOT_NECESSARY   -> DatasetImportStatus.Complete
      ReimportIndicator.NEEDS_REIMPORT        -> DatasetImportStatus.Queued
      ReimportIndicator.REIMPORT_NOT_POSSIBLE -> {
        if (ctx.hasInstallReadyData() && ctx.hasManifest())
          DatasetImportStatus.Complete
        else if (ctx.hasUploadError())
          null
        else
          DatasetImportStatus.Failed
      }
    }

  private fun ensureUninstalled(ctx: ReconcilerTarget) {
    if (!ctx.hasMeta()) {
      ctx.logger.error("dataset does not have a meta file, cannot uninstall")
      return
    }

    val dataset = cacheDB.requireCacheDatasetRecord(ctx)

    if (!dataset.isDeleted) {
      ctx.safeExec("failed to mark dataset as deleted in cache db") {
        cacheDB.withTransaction { it.updateDatasetDeleted(ctx.datasetId.toDatasetID(), true) }
      }
    }

    if (!AppDbUtils.isFullyUninstalled(appDB, ctx))
      fireUninstallEvent(ctx)
  }

  private fun handleHasUpload(ctx: ReconcilerTarget) {
    // If an import-ready.zip file exists for the dataset then we can delete the
    // raw upload file and ensure that the upload control table in the cache db
    // indicates that the upload was successfully processed.
    if (ctx.hasImportReadyData()) {
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

  private fun synchronizeUploadStatus(ctx: ReconcilerTarget, status: DatasetUploadStatus) {
    cacheDB.withTransaction {
      ctx.meta
        ?.created
        ?.let { created -> it.updateMetaSyncControl(DatasetID(ctx.datasetId), created) }
      it.upsertUploadStatus(DatasetID(ctx.datasetId), status)
    }
  }

  private fun runDatasetSync(ctx: ReconcilerTarget) {
    val syncStatus = ctx.checkSyncStatus()

    if (syncStatus.isMetaOutOfSync)
      fireUpdateMetaEvent(ctx)

    if (syncStatus.isSharesOutOfSync)
      fireShareEvent(ctx)

    // If we've already fired an import event, then an install event will be
    // triggered by that import, meaning there is no need to fire an install
    // event here.
    if (!ctx.haveFiredImportEvent && syncStatus.isInstallOutOfSync)
      fireInstallEvent(ctx)
    else if (!syncStatus.isInstallOutOfSync && ctx.meta!!.revisionHistory != null && isInstalled(ctx))
      ensureRevisionFlags(ctx)
  }

  private fun ReconcilerTarget.checkSyncStatus(): SyncIndicator {
    val cacheDBSyncControl = cacheDB.requireSyncControl(this)

    val metaTimestamp = maxOf(
      datasetDirectory.getMetaTimestamp() ?: cacheDBSyncControl.metaUpdated,
      datasetDirectory.getLatestVariablePropertiesTimestamp() ?: cacheDBSyncControl.metaUpdated,
    )

    val installDataTimestamp = datasetDirectory.getInstallReadyTimestamp()
      ?: cacheDBSyncControl.dataUpdated

    val latestShareTimestamp = datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)

    val indicator = SyncIndicator(
      /*metaOutOfSync    =*/ cacheDBSyncControl.metaUpdated.isBefore(metaTimestamp),
      /*sharesOutOfSync  =*/ cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp),
      /*installOutOfSync =*/ cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp),
    )

    if (indicator.isFullyOutOfSync)
      return indicator

    meta!!.installTargets.forEach { projectID ->
      val appDB = appDB.accessor(projectID, meta!!.type)

      if (appDB == null) {
        logger.warn("skipping dataset state comparison for disabled target {}", projectID)
        return@forEach
      }

      val sync = AppDbUtils.selectSyncControl(appDB, this)
        // If the dataset sync record does not exist at all, then fire a sync
        // action for shares and install.  The share sync is most likely going
        // to be processed before the installation, so the shares probably won't
        // make it to the installation target until the next big reconciler run.
        ?: return SyncIndicator(true, true, true)

      if (!indicator.isMetaOutOfSync && sync.metaUpdated.isBefore(metaTimestamp))
        indicator.isMetaOutOfSync = true

      if (!indicator.isSharesOutOfSync && sync.sharesUpdated.isBefore(latestShareTimestamp))
        indicator.isSharesOutOfSync = true

      if (!indicator.isInstallOutOfSync && sync.dataUpdated.isBefore(installDataTimestamp))
        indicator.isInstallOutOfSync = true

      if (indicator.isFullyOutOfSync)
        return indicator
    }

    return indicator
  }

  private fun tryReimport(ctx: ReconcilerTarget) {
    cacheDB.updateImportStatus(ctx, DatasetImportStatus.Queued)
    cacheDB.dropImportMessages(ctx)
    fireImportEvent(ctx)
  }

  private fun isInstalled(ctx: ReconcilerTarget) =
    ctx.meta!!.installTargets.all {
      (AppDB().accessor(it, ctx.meta!!.type) ?: return@all true)
        .selectDatasetInstallMessage(ctx.datasetId.toDatasetID(), InstallType.Data)?.status == InstallStatus.COMPLETE
    }

  private fun ensureRevisionFlags(ctx: ReconcilerTarget) {
    val targets = ArrayList<DatasetID>(ctx.meta!!.revisionHistory!!.revisions.size)
    ctx.meta!!.revisionHistory!!.revisions
      .asSequence()
      .sortedByDescending { it.timestamp }
      .forEach { targets.add(it.revisionID) }

    targets.forEach {
      datasetManager.getDatasetDirectory(ctx.userId, it)
        .getRevisedFlag()
        .also { flag -> if (!flag.exists()) flag.create() }
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
  private fun shouldReimport(ctx: ReconcilerTarget) =
    when {
      // If we don't have an import-ready file, we can't rerun the import
      // process
      !ctx.hasImportReadyData() -> ReimportIndicator.REIMPORT_NOT_POSSIBLE

      // If the import was found to be invalid, then it's a data problem and no
      // amount of reimport attempts will help.
      cacheDB.isImportInvalid(ctx) -> ReimportIndicator.REIMPORT_NOT_POSSIBLE

      // If we failed the last import attempt, then only rerun the import if we
      // have no import messages so that we can repopulate that table to
      // indicate to the user what happened.
      cacheDB.isImportFailed(ctx) -> if (cacheDB.isMissingImportMessage(ctx)) {
        ctx.logger.warn("import failed, but we have no error message; marking dataset as needing a reimport to populate the error message")
        ReimportIndicator.NEEDS_REIMPORT
      } else {
        ctx.logger.info("import failed, reimport attempt not necessary")
        ReimportIndicator.REIMPORT_NOT_NECESSARY
      }

      // If we are missing the install-ready file and/or the manifest file then
      // we need to rerun the import to get those files back.
      !(ctx.hasInstallReadyData() && ctx.hasManifest()) -> ReimportIndicator.NEEDS_REIMPORT

      // We have an import-ready file, we have no failed import record, we have
      // both the install-ready and manifest files.  There is no need to rerun
      // the import process.
      else -> ReimportIndicator.REIMPORT_NOT_NECESSARY
    }

  // region Kafka

  private fun fireImportEvent(ctx: ReconcilerTarget) {
    ctx.logger.info("firing import event")
    ctx.safeExec("failed to fire import trigger") {
      eventRouter.sendImportTrigger(ctx.userId, ctx.datasetId.toDatasetID(), ctx.source)
      ctx.haveFiredImportEvent = true
    }
  }

  private fun fireUpdateMetaEvent(ctx: ReconcilerTarget) {
    ctx.logger.info("firing update meta event")
    ctx.safeExec("failed to fire update-meta trigger") {
      eventRouter.sendUpdateMetaTrigger(ctx.userId, ctx.datasetId.toDatasetID(), ctx.source)
      ctx.haveFiredMetaUpdateEvent = true
    }
  }

  private fun fireShareEvent(ctx: ReconcilerTarget) {
    ctx.logger.info("firing share event")
    ctx.safeExec("failed to send share trigger") {
      eventRouter.sendShareTrigger(ctx.userId, ctx.datasetId.toDatasetID(), ctx.source)
      ctx.haveFiredShareEvent = true
    }
  }

  private fun fireInstallEvent(ctx: ReconcilerTarget) {
    ctx.logger.info("firing data install event")
    ctx.safeExec("failed to send install-data trigger") {
      eventRouter.sendInstallTrigger(ctx.userId, ctx.datasetId.toDatasetID(), ctx.source)
      ctx.haveFiredInstallEvent = true
    }
  }

  private fun fireUninstallEvent(ctx: ReconcilerTarget) {
    ctx.logger.info("firing soft-delete event")
    ctx.safeExec("failed to send soft-delete trigger") {
      eventRouter.sendSoftDeleteTrigger(ctx.userId, ctx.datasetId.toDatasetID(), ctx.source)
      ctx.haveFiredUninstallEvent = true
    }
  }

  // endregion Kafka
}
