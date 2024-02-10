package vdi.lane.reconciliation

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.isNull
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import vdi.component.metrics.Metrics

object DatasetReconciler {
  private val logger = LoggerFactory.getLogger(javaClass)

  @JvmStatic
  fun reconcile(
    userID: UserID,
    datasetID: DatasetID,
    datasetDirectory: DatasetDirectory,
    eventRouter: KafkaRouter,
  ) {
    logger.info("beginning reconciliation for dataset {}/{}", userID, datasetID)
    try {
      ReconciliationState(userID, datasetID, datasetDirectory, eventRouter).reconcile()
    } finally {
      logger.info("reconciliation completed for dataset {}/{}", userID, datasetID)
    }
  }

  private fun ReconciliationState.reconcile() {
    val projects = loadProjects() ?: return

    if (hasDeletedFlag) {
      handleDeleted(projects)
      return
    }

    // The upload file should only exist before an attempt has been made to
    // process the raw upload into an import-ready form.
    if (hasRawUpload)
      handleHasUpload()

    if (hasImportReadyZip)
      handleHasImportable()

    if (hasInstallReadyZip)
      handleHasInstallable()

    // If we have no install-ready zip, but for some reason we _do_ have a
    // manifest file.  This is likely due to a failed deletion in the pruner,
    // but manual investigation should be performed to confirm.
    else if (hasManifestFile)
      handleMissingInstallableWithManifest()

    if (hasMetaFile && hasInstallReadyZip)
      runSync(projects)
  }

  private fun ReconciliationState.handleDeleted(projects: Iterable<ProjectID>) {
    CacheDB.selectDataset(datasetID)?.also {
      if (!it.isDeleted) {
        try {
          CacheDB.withTransaction { db -> db.updateDatasetDeleted(datasetID, true) }
        } catch (e: Throwable) {
          logError("failed to mark dataset $userID/$datasetID as deleted in cache db", e)
        }
      }
    } ?: logWarning("dataset {}/{} does not have a record in the cache db", userID, datasetID)

    if (!isFullyUninstalled(projects))
      fireUninstallEvent()
  }

  private fun ReconciliationState.handleHasUpload() {
    // If an import-ready.zip file exists for the dataset then we can delete
    // the raw upload file and ensure that the upload control table in the
    // cache db indicates that the upload was successfully processed.
    if (hasImportReadyZip) {
      try { datasetDirectory.deleteUploadFile() }
      catch (e: Exception) {
        logError("failed to delete raw upload file from dataset $userID/$datasetID", e)
      }

      // TODO: Write completed status to upload control table in cache db
    }

    // If no import-ready.zip file exists for the dataset, then check the
    // cache db upload control table.  If the control table indicates that the
    // upload processing failed then delete the raw upload file.  If the
    // control table indicates that the upload processing is queued or
    // in progress then fire an upload event to make sure the upload is
    // processed.  Best case, the upload is already being processed and the
    // upload handler will reject the job, worst case we caught an interrupted
    // upload job.
    else {
      // TODO: Implement with robust async upload
    }
  }

  private fun ReconciliationState.handleHasImportable() {
    // If an install-ready.zip file and manifest file exist for the dataset
    // then ensure the import control table indicates that the import for the
    // dataset was successful.
    if (hasInstallReadyZip && hasManifestFile) {
      updateCacheDBImportStatus(DatasetImportStatus.Complete)
      syncCacheDBFileTables()
    }

    // If one of the install-ready.zip file or manifest file are missing then
    // the import process was interrupted.  Reset the import control table in
    // the cache db to queued and fire an import event.
    else if (hasInstallReadyZip || hasManifestFile) {
      if (hasInstallReadyZip)
        logWarning("dataset {}/{} is missing its manifest file", userID, datasetID)
      else
        logWarning("dataset {}/{} is missing its install-ready file", userID, datasetID)

      tryFireImportEvent()
    }

    // If both the install-ready.zip and manifest file are missing for the
    // dataset, check the import control table in the cache database.  If the
    // import is marked as failed in the control table then there is nothing
    // more to do.  If the import is not marked as failed in the import
    // control table, then fire an import event to re-attempt the import.
    else {
      if (CacheDB.selectImportControl(datasetID) != DatasetImportStatus.Failed) {
        tryFireImportEvent()
      }
    }
  }

  private fun ReconciliationState.handleHasInstallable() {
    if (!hasImportReadyZip) {
      logError("dataset $userID/$datasetID has an install-ready zip but no import-ready zip, reimporting is not possible for this dataset")

      // If no import-ready file exists, then we would not have checked for
      // the existence of the manifest file yet.
      if (!hasManifestFile)
        logError("dataset $userID/$datasetID has an install-ready zip but is missing its import-ready zip and manifest file; it will not be possible to populate the dataset files tables in the cache db")
    }

    // We have an import-ready file and an install-ready file
    else {
      // If we fired an import event in this reconciliation then don't
      // override the status here.
      if (!haveFiredImportEvent)
        updateCacheDBImportStatus(DatasetImportStatus.Complete)
    }
  }

  private fun ReconciliationState.handleMissingInstallableWithManifest() {
    if (!hasImportReadyZip)
      logError("dataset $userID/$datasetID has a manifest file but no install-ready or import-ready files, reimporting and (re)installing are impossible for this dataset")

    // We have an import-ready file and no install-ready file.
    //
    // There is no need to log anything extra here as the primary
    // `hasImportReadyZip` if-block above will have logged an error for the
    // missing install-ready file.
    else
      tryFireImportEvent()
  }

  private fun ReconciliationState.runSync(projects: Iterable<ProjectID>) {
    val syncStatus = checkSyncStatus(projects)

    if (syncStatus.metaOutOfSync)
      fireUpdateMetaEvent()

    // If we have an install-ready zip, then the dataset imported successfully
    // which means it is safe to push shares to the install targets.
    if (hasInstallReadyZip && syncStatus.sharesOutOfSync)
      fireShareEvent()

    // If we've already fired an import event, then an install event will be
    // triggered by that import, meaning there is no need to fire an install
    // event here.
    if (!haveFiredImportEvent && syncStatus.installOutOfSync)
      fireInstallEvent()
  }

  private fun ReconciliationState.checkSyncStatus(projects: Iterable<ProjectID>): SyncIndicator {
    val cachedDatasetRecord = CacheDB.selectDataset(datasetID)

    if (cachedDatasetRecord == null) {
      logError("dataset $userID/$datasetID could not be found in the cache database, cannot perform sync check")
      return SyncIndicator(metaOutOfSync = false, sharesOutOfSync = false, installOutOfSync = false)
    }

    val cacheDBSyncControl = getOrCreateCacheDBSyncControl()
      ?: return SyncIndicator(metaOutOfSync = false, sharesOutOfSync = false, installOutOfSync = false)

    val metaTimestamp = datasetDirectory.getMetaTimestamp() ?: cacheDBSyncControl.metaUpdated
    val latestShareTimestamp = datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)
    val installDataTimestamp = datasetDirectory.getInstallReadyTimestamp() ?: cacheDBSyncControl.dataUpdated

    var metaOutOfSync = cacheDBSyncControl.metaUpdated.isBefore(metaTimestamp)
    var sharesOutOfSync = cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp)
    var installOutOfSync = cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp)

    if (metaOutOfSync && sharesOutOfSync && installOutOfSync)
      return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)

    projects.forEach { projectID ->
      val appDB = AppDB.accessor(projectID)

      if (appDB == null) {
        logWarning("skipping dataset state comparison for dataset {}/{}, project {} due to the target project being disabled", userID, datasetID, projectID)
        return@forEach
      }

      val sync = appDB.selectDatasetSyncControlRecord(datasetID)
        // If the dataset sync record does not exist at all, then fire a sync
        // action for shares and install.  The share sync is most likely going
        // to be processed before the install, so the shares probably won't make
        // it to the install target until the next big reconciler run.
        ?: return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)

      if (!metaOutOfSync && sync.metaUpdated.isBefore(metaTimestamp))
        metaOutOfSync = true

      if (!sharesOutOfSync && sync.sharesUpdated.isBefore(latestShareTimestamp))
        sharesOutOfSync = true

      if (!installOutOfSync && sync.dataUpdated.isBefore(installDataTimestamp))
        installOutOfSync = true

      if (metaOutOfSync && sharesOutOfSync && installOutOfSync)
        return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)
    }

    return SyncIndicator(metaOutOfSync, sharesOutOfSync, installOutOfSync)
  }

  private fun ReconciliationState.getOrCreateCacheDBSyncControl() =
    try {
      CacheDB.selectSyncControl(datasetID) or {
        VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp)
          .also { CacheDB.withTransaction { db -> db.tryInsertSyncControl(it) } }
      }
    } catch (e: Throwable) {
      logError("failed attempt to get or create cache db sync control record for dataset $userID/$datasetID", e)
      null
    }

  private fun ReconciliationState.updateCacheDBImportStatus(status: DatasetImportStatus) {
    try {
      CacheDB.withTransaction { it.updateImportControl(datasetID, status) }
    } catch (e: Throwable) {
      logError("failed to update dataset import status to $status for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.tryFireImportEvent() {
    if (haveFiredImportEvent)
      return

    updateCacheDBImportStatus(DatasetImportStatus.Queued)
    fireImportEvent()
  }

  private fun ReconciliationState.syncCacheDBFileTables() {
    val manifest = try {
      datasetDirectory.getManifestFile().load()
    } catch (e: Throwable) {
      logError("failed to load manifest file for dataset $userID/$datasetID, cannot sync file list with cache-db", e)
      return
    }

    if (manifest == null) {
      logError("manifest file no longer exists for dataset $userID/$datasetID, cannot sync file list with cache-db")
      return
    }

    try {
      CacheDB.withTransaction {
        it.insertUploadFiles(datasetID, manifest.inputFiles)
        it.tryInsertInstallFiles(datasetID, manifest.dataFiles)
      }
    } catch (e: Throwable) {
      logError("failed to write file listing to cache db for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.fireImportEvent() {
    try {
      logger.info("firing import event for dataset {}/{}", userID, datasetID)
      eventRouter.sendImportTrigger(userID, datasetID)
      haveFiredImportEvent = true
    } catch (e: Throwable) {
      logError("failed to fire import trigger for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.fireUpdateMetaEvent() {
    try {
      logger.info("firing update meta event for dataset {}/{}", userID, datasetID)
      eventRouter.sendUpdateMetaTrigger(userID, datasetID)
      haveFiredMetaEvent = true
    } catch (e: Throwable) {
      logError("failed to send update-meta trigger for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.fireShareEvent() {
    try {
      logger.info("firing share event for dataset {}/{}", userID, datasetID)
      eventRouter.sendShareTrigger(userID, datasetID)
      haveFiredShareEvent = true
    } catch (e: Throwable) {
      logError("failed to send share trigger for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.fireInstallEvent() {
    try {
      logger.info("firing data install event for dataset {}/{}", userID, datasetID)
      eventRouter.sendInstallTrigger(userID, datasetID)
      haveFiredInstallEvent = true
    } catch (e: Throwable) {
      logError("failed to send install-data trigger for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.fireUninstallEvent() {
    try {
      logger.info("firing soft-delete/uninstall event for dataset {}/{}", userID, datasetID)
      eventRouter.sendSoftDeleteTrigger(userID, datasetID)
      haveFiredUninstallEvent = true
    } catch (e: Throwable) {
      logError("failed to send soft-delete/uninstall trigger for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.isFullyUninstalled(projects: Iterable<ProjectID>): Boolean {
    projects.forEach { projectID ->
      val appDB = AppDB.accessor(projectID)

      if (appDB == null) {
        logWarning("cannot check installation status of dataset {}/{} in project {} due to the target being disabled in the service config", userID, datasetID, projectID)
        return false
      }

      val targetRecord = appDB.selectDataset(datasetID)

      if (targetRecord == null) {
        logWarning("attempted to check install status for dataset {}/{} in project {} but no such dataset record could be found", userID, datasetID, projectID)
        return false
      }

      if (targetRecord.isDeleted != DeleteFlag.DeletedAndUninstalled) {
        return false
      }
    }

    return true
  }

  private fun ReconciliationState.loadMeta(): VDIDatasetMeta? =
    try {
      datasetDirectory.getMetaFile().load()
    } catch (e: Throwable) {
      logError("failed to load metadata for dataset $userID/$datasetID", e)
      null
    }

  private fun ReconciliationState.loadManifest(): VDIDatasetManifest? =
    try {
      datasetDirectory.getManifestFile().load()
    } catch (e: Throwable) {
      logError("failed to load manifest for dataset $userID/$datasetID", e)
      null
    }

  private fun ReconciliationState.loadProjects(): Iterable<ProjectID>? {
    if (hasMetaFile) {
      return loadMeta()?.projects?.let {
        it.ifEmpty {
          logError("dataset $userID/$datasetID cannot be reconciled, it has no projects listed in its meta file")
          null
        }
      }
    } else {
      logError("received a reconciliation event for dataset $userID/$datasetID which has no meta file")
    }

    val tmp = CacheDB.selectDataset(datasetID)

    if (tmp.isNull()) {
      logError("dataset $userID/$datasetID cannot be reconciled, it has no meta file or cache db record")
      return null
    }

    if (tmp.projects.isEmpty()) {
      logError("dataset $userID/$datasetID cannot be reconciled, it has no meta file and no projects listed in the cache db")
      return null
    }

    return tmp.projects
  }

  private fun logWarning(message: String, vararg objects: Any?) {
    Metrics.ReconciliationHandler.warnings.inc()
    logger.warn(message, *objects)
  }

  private fun logError(message: String, error: Throwable? = null) {
    Metrics.ReconciliationHandler.errors.inc()
    if (error == null)
      logger.error(message)
    else
      logger.error(message, error)
  }
}


private class SyncIndicator(
  val metaOutOfSync: Boolean,
  val sharesOutOfSync: Boolean,
  val installOutOfSync: Boolean,
)

private class ReconciliationState(
  val userID: UserID,
  val datasetID: DatasetID,
  val datasetDirectory: DatasetDirectory,
  val eventRouter: KafkaRouter,
) {
  val hasMetaFile by lazy { datasetDirectory.hasMetaFile() }

  val hasDeletedFlag by lazy { datasetDirectory.hasDeleteFlag() }

  val hasRawUpload by lazy { datasetDirectory.hasUploadFile() }

  val hasImportReadyZip by lazy { datasetDirectory.hasImportReadyFile() }

  val hasInstallReadyZip by lazy { datasetDirectory.hasInstallReadyFile() }

  val hasManifestFile by lazy { datasetDirectory.hasManifestFile() }

  var haveFiredMetaEvent = false

  var haveFiredUploadEvent = false

  var haveFiredImportEvent = false

  var haveFiredInstallEvent = false

  var haveFiredShareEvent = false

  var haveFiredUninstallEvent = false
}