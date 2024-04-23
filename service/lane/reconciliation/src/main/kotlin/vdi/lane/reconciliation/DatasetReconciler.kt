package vdi.lane.reconciliation

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDBAccessor
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetImpl
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetMetaImpl
import vdi.component.db.cache.withTransaction
import vdi.component.kafka.EventSource
import vdi.component.kafka.router.KafkaRouter
import vdi.component.metrics.Metrics
import vdi.component.s3.DatasetDirectory
import vdi.component.s3.DatasetManager
import java.time.OffsetDateTime

internal class DatasetReconciler(
  private val cacheDB: CacheDB = CacheDB(),
  private val appDB: AppDB = AppDB(),
  private val eventRouter: KafkaRouter,
  private val datasetManager: DatasetManager,
) {
  private val logger = LoggerFactory.getLogger(javaClass)

  // TODO: If the import-ready file is newer than the install-ready or manifest
  //       files then we should fire an import event.
  // TODO: If the raw upload file is newer than the import-ready file then we
  //       should fire an upload processing event.

  fun reconcile(userID: UserID, datasetID: DatasetID, source: EventSource) {
    logger.info("beginning reconciliation for dataset {}/{}", userID, datasetID)
    try {
      ReconciliationState(datasetManager.getDatasetDirectory(userID, datasetID), source).reconcile()
      logger.info("reconciliation completed for dataset {}/{}", userID, datasetID)
    } catch (e: CriticalReconciliationError) {
      logger.error("reconciliation failed for dataset {}/{}", userID, datasetID)
    } catch (e: Throwable) {
      Metrics.ReconciliationHandler.errors.inc()
      logger.error("reconciliation failed for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.reconcile() {
    // Ensure we have a meta json file.  This method will throw an exception if
    // no meta file exists.
    loadMeta()

    // Make sure the cache db has at least the base records for this dataset.
    tryInitCacheDB()

    if (haveDeleteFlag()) {
      handleDeleted()
      return
    }

    // The upload file should only exist before an attempt has been made to
    // process the raw upload into an import-ready form.
    if (haveRawUpload())
      handleHasUpload()

    val reimport = shouldReimport()

    if (reimport == ReimportIndicator.NeedReimport) {
      tryReimport()
      return
    }

    if (!haveImportableFile())
      logError("missing import-ready file")

    if (reimport == ReimportIndicator.ReimportNotNeeded)
      if (getCacheImportControl() == DatasetImportStatus.Queued)
        updateCacheDBImportStatus(DatasetImportStatus.Complete)

    if (reimport != ReimportIndicator.ReimportNotPossible) {
      if (!haveInstallableFile())
        logError("missing install-ready file")

      if (!haveManifestFile())
        logError("missing manifest file")
    }

    runSync()

    if (!haveFiredAnyEvents)
      logInfo("no events fired")
  }

  private fun ReconciliationState.handleDeleted() {
    val dataset = requireCacheDatasetRecord()

    if (!dataset.isDeleted) {
      safeExec("failed to mark dataset as deleted in cache db") {
        cacheDB.withTransaction { it.updateDatasetDeleted(datasetID, true) }
      }
    }

    if (!isFullyUninstalled(getProjects()))
      fireUninstallEvent()
  }

  private fun ReconciliationState.handleHasUpload() {
    // If an import-ready.zip file exists for the dataset then we can delete the
    // raw upload file and ensure that the upload control table in the cache db
    // indicates that the upload was successfully processed.
    if (haveImportableFile()) {
      safeExec("failed to delete raw upload file") { datasetDirectory.deleteUploadFile() }
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

  private fun ReconciliationState.runSync() {
    val syncStatus = checkSyncStatus()

    if (syncStatus.metaOutOfSync)
      fireUpdateMetaEvent()

    if (syncStatus.sharesOutOfSync)
      fireShareEvent()

    // If we've already fired an import event, then an install event will be
    // triggered by that import, meaning there is no need to fire an install
    // event here.
    if (!haveFiredImportEvent && syncStatus.installOutOfSync)
      fireInstallEvent()
  }

  private fun ReconciliationState.checkSyncStatus(): SyncIndicator {
    val cacheDBSyncControl = requireCacheDBSyncControl()

    val metaTimestamp = datasetDirectory.getMetaTimestamp() ?: cacheDBSyncControl.metaUpdated
    val latestShareTimestamp = datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)
    val installDataTimestamp = datasetDirectory.getInstallReadyTimestamp() ?: cacheDBSyncControl.dataUpdated

    var metaOutOfSync = cacheDBSyncControl.metaUpdated.isBefore(metaTimestamp)
    var sharesOutOfSync = cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp)
    var installOutOfSync = cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp)

    if (metaOutOfSync && sharesOutOfSync && installOutOfSync)
      return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)

    getProjects().forEach { projectID ->
      val appDB = appDB.accessor(projectID)

      if (appDB == null) {
        logWarning("skipping dataset state comparison for dataset {}/{}, project {} due to the target project being disabled", userID, datasetID, projectID)
        return@forEach
      }

      val sync = appDB.selectSyncControl()
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

  private fun ReconciliationState.tryReimport() {
    updateCacheDBImportStatus(DatasetImportStatus.Queued)
    dropImportMessages()
    fireImportEvent()
  }

  /**
   * Dataset reimport is needed in the following cases:
   *
   * 1. Install-ready and/or manifest files are missing AND import has not
   *    already failed
   * 2. Import has failed but there are no import messages.
   * 3. TODO: import-ready file is newer than installable and/or manifest files
   */
  private fun ReconciliationState.shouldReimport() =
    when {
      // If we don't have an import-ready file, we can't rerun the import
      // process
      !haveImportableFile() -> ReimportIndicator.ReimportNotPossible

      // If the import was found to be invalid, then it's a data problem and no
      // amount of reimport attempts will help.
      invalidImport() -> ReimportIndicator.ReimportNotPossible

      // If we failed the last import attempt, then only rerun the import if we
      // have no import messages so that we can repopulate that table to
      // indicate to the user what happened.
      failedImport() -> if (missingImportMessage()) ReimportIndicator.NeedReimport else ReimportIndicator.ReimportNotNeeded

      // If we are missing the install-ready file and/or the manifest file then
      // we need to rerun the import to get those files back.
      !(haveInstallableFile() && haveManifestFile()) -> ReimportIndicator.NeedReimport

      // We have an import-ready file, we have no failed import record, we have
      // both the install-ready and manifest files.  There is no need to rerun
      // the import process.
      else -> ReimportIndicator.ReimportNotNeeded
    }

  private enum class ReimportIndicator {
    /**
     * Reimporting the target dataset is not possible due to the data being
     * invalid or the state in MinIO being broken (missing import-ready.zip).
     */
    ReimportNotPossible,

    /**
     * Reimport needed when:
     * * A previous import attempt failed, but the cache-db does not have a
     *   message recorded.  In this case we re-run to provide an error message
     *   for the user.
     * * The import is not marked as failed but we do not have both an
     *   install-ready.zip file and vdi-manifest.json file
     */
    NeedReimport,

    /**
     * We already have an import-ready.zip and vdi-manifest.json or the import
     * previously failed and we have an error message for the user.
     */
    ReimportNotNeeded,
  }

  // region S3
  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     S3 Operations
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private fun ReconciliationState.loadMeta() =
    cMeta.computeIfAbsent {
      safeExec("failed to load metadata") { datasetDirectory.getMetaFile().load() }
    }.require("missing meta json file")

  private fun ReconciliationState.haveManifestFile() =
    loadManifest() != null

  private fun ReconciliationState.loadManifest() =
    cManifest.computeIfAbsent {
      safeExec("failed to load manifest") { datasetDirectory.getManifestFile().load() }
    }

  private fun ReconciliationState.haveDeleteFlag() =
    cHasDeleteFlag.computeIfAbsent {
      safeExec("failed to check for deleted flag") { datasetDirectory.hasDeleteFlag() }
    }

  private fun ReconciliationState.haveImportableFile() =
    cHasImportable.computeIfAbsent {
      safeExec("failed to check for import-ready file") { datasetDirectory.hasImportReadyFile() }
    }

  private fun ReconciliationState.haveInstallableFile() =
    cHasInstallable.computeIfAbsent {
      safeExec("failed to check for install-ready file") { datasetDirectory.hasInstallReadyFile() }
    }

  private fun ReconciliationState.haveRawUpload() =
    cHasRawUpload.computeIfAbsent {
      safeExec("failed to check for raw upload file") { datasetDirectory.hasUploadFile() }
    }

  private fun ReconciliationState.getProjects() = loadMeta().projects

  // endregion S3

  // region Kafka
  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     Kafka Operations
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private fun ReconciliationState.fireImportEvent() {
    logger.info("firing import event for dataset {}/{}", userID, datasetID)
    safeExec("failed to fire import trigger") {
      eventRouter.sendImportTrigger(userID, datasetID, source)
      haveFiredImportEvent = true
    }
  }

  private fun ReconciliationState.fireUpdateMetaEvent() {
    logger.info("firing update meta event for dataset {}/{}", userID, datasetID)
    safeExec("failed to fire update-meta trigger") {
      eventRouter.sendUpdateMetaTrigger(userID, datasetID, source)
      haveFiredMetaUpdateEvent = true
    }
  }

  private fun ReconciliationState.fireShareEvent() {
    logger.info("firing share event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send share trigger") {
      eventRouter.sendShareTrigger(userID, datasetID, source)
      haveFiredShareEvent = true
    }
  }

  private fun ReconciliationState.fireInstallEvent() {
    logger.info("firing data install event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send install-data trigger") {
      eventRouter.sendInstallTrigger(userID, datasetID, source)
      haveFiredInstallEvent = true
    }
  }

  private fun ReconciliationState.fireUninstallEvent() {
    logger.info("firing soft-delete/uninstall event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send soft-delete trigger") {
      eventRouter.sendSoftDeleteTrigger(userID, datasetID, source)
      haveFiredUninstallEvent = true
    }
  }

  // endregion Kafka

  // region AppDB
  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     App DB Operations
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  context (ReconciliationState)
  private fun AppDBAccessor.selectAppDatasetRecord() =
    safeExec({ "failed to fetch dataset record from $project" }) { selectDataset(datasetID) }

  context (ReconciliationState)
  private fun AppDBAccessor.selectSyncControl() =
    safeExec({ "failed to fetch dataset sync control record from $project" }) { selectDatasetSyncControlRecord(datasetID) }

  private fun ReconciliationState.isFullyUninstalled(projects: Iterable<ProjectID>): Boolean {
    projects.forEach { projectID ->
      val appDB = appDB.accessor(projectID)

      if (appDB == null) {
        logWarning("cannot check installation status of dataset {}/{} in project {} due to the target being disabled in the service config", userID, datasetID, projectID)
        return false
      }

      appDB.isUninstalled() || return false
    }

    return true
  }

  context (ReconciliationState)
  private fun AppDBAccessor.isUninstalled(): Boolean {
    val targetRecord = selectAppDatasetRecord()

    // If the target record does not exist, then either VDI has been configured
    // to point at a clean database, the target database has been wiped, or
    // something has gone terribly wrong.  We'll assume it was intentional here
    // though and consider the dataset to be already uninstalled.
    if (targetRecord == null) {
      logWarning(
        "attempted to check install status for dataset {}/{} in project {} but no such dataset record could be found;" +
          " assuming clean database and treating dataset as if it has been successfully uninstalled",
        userID,
        datasetID,
        project
      )
      return true
    }

    if (targetRecord.isDeleted != DeleteFlag.DeletedAndUninstalled) {
      return false
    }

    return true
  }

  // endregion AppDB

  // region CacheDB
  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     Cache DB Operations
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private fun ReconciliationState.failedImport(refresh: Boolean = false) =
    getCacheImportControl(refresh) == DatasetImportStatus.Failed

  private fun ReconciliationState.invalidImport(refresh: Boolean = false) =
    getCacheImportControl(refresh) == DatasetImportStatus.Invalid

  private fun ReconciliationState.missingImportMessage(refresh: Boolean = false) =
    safeExec("failed to fetch import messages from cache db") {
      if (refresh || !cImportMessages.hasBeenComputed)
        cImportMessages.value = cacheDB.selectImportMessages(datasetID).isEmpty()
      cImportMessages.value
    }

  private fun ReconciliationState.getCacheImportControl(refresh: Boolean = false) =
    safeExec("failed to fetch import control from cache db") {
      if (refresh || !cImportControl.hasBeenComputed)
        cImportControl.value = cacheDB.selectImportControl(datasetID)
      cImportControl.value
    }

  private fun ReconciliationState.updateCacheDBImportStatus(status: DatasetImportStatus) =
    try {
      cacheDB.withTransaction { it.upsertImportControl(datasetID, status) }
    } catch (e: Throwable) {
      logError("$userID/$datasetID: failed to update dataset import status to $status", e)
    }

  private fun ReconciliationState.getCacheDatasetRecord() =
    safeExec("failed to query cache db for dataset record") { cacheDB.selectDataset(datasetID) }

  private fun ReconciliationState.requireCacheDatasetRecord() =
    getCacheDatasetRecord().require("could not find dataset record in cache db")

  private fun ReconciliationState.requireCacheDBSyncControl() =
    safeExec("failed to load sync control record from cache db") { cacheDB.selectSyncControl(datasetID) }
      .require("could not find dataset sync control record")

  private fun ReconciliationState.dropImportMessages() =
    safeExec("failed to delete import messages") { cacheDB.withTransaction { it.deleteImportMessages(datasetID) } }

  private fun ReconciliationState.tryInitCacheDB() {
    val meta = loadMeta()
    val manifest = loadManifest()

    val importStatus = when (shouldReimport()) {
      ReimportIndicator.ReimportNotNeeded -> DatasetImportStatus.Complete
      ReimportIndicator.NeedReimport -> DatasetImportStatus.Queued
      ReimportIndicator.ReimportNotPossible -> {
        if (haveInstallableFile() && haveManifestFile())
          DatasetImportStatus.Complete
        else
          DatasetImportStatus.Failed
      }
    }

    safeExec("failed to soft-initialize cache db records") {
      cacheDB.withTransaction { db ->
        db.tryInsertDataset(DatasetImpl(
          datasetID = datasetID,
          typeName = meta.type.name,
          typeVersion = meta.type.version,
          ownerID = userID,
          isDeleted = haveDeleteFlag(),
          created = meta.created,
          importStatus = DatasetImportStatus.Queued, // this value is not used for inserts
          origin = meta.origin,
          inserted = OffsetDateTime.now(),
        ))

        db.tryInsertDatasetMeta(
          DatasetMetaImpl(
            datasetID = datasetID,
            visibility = meta.visibility,
            name = meta.name,
            summary = meta.summary,
            description = meta.description,
            sourceURL = meta.sourceURL,
          )
        )

        db.tryInsertDatasetProjects(datasetID, meta.projects)

        db.tryInsertImportControl(datasetID, importStatus)

        if (importStatus == DatasetImportStatus.Failed)
          db.tryInsertImportMessages(datasetID, "dataset has no import-ready file and is in an incomplete state due to the absence of the install-ready data and/or manifest")

        db.tryInsertSyncControl(
          VDISyncControlRecord(
            datasetID = datasetID,
            sharesUpdated = OriginTimestamp,
            dataUpdated = OriginTimestamp,
            metaUpdated = OriginTimestamp,
          )
        )

        if (manifest != null) {
          db.tryInsertUploadFiles(datasetID, manifest.inputFiles)
          db.tryInsertInstallFiles(datasetID, manifest.dataFiles)
        }
      }
    }
  }

  // endregion CacheDB

  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     Enforcement
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private inline fun <T> ReconciliationState.safeExec(msg: () -> String, fn: () -> T) =
    try {
      fn()
    } catch (e: CriticalReconciliationError) {
      throw e
    } catch (e: Throwable) {
      logError("$userID/$datasetID: " + msg(), e)
      throw CriticalReconciliationError()
    }

  private inline fun <T> ReconciliationState.safeExec(msg: String, fn: () -> T) =
    try {
      fn()
    } catch (e: CriticalReconciliationError) {
      throw e
    } catch (e: Throwable) {
      logError("$userID/$datasetID: $msg", e)
      throw CriticalReconciliationError()
    }

  context (ReconciliationState)
  private fun <T> T?.require(msg: String): T =
    if (this == null) {
      logError("$userID/$datasetID: $msg")
      throw CriticalReconciliationError()
    } else {
      this
    }

  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     Logging Helpers
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private inline val ReconciliationState.datasetRef
    get() = "$userID/$datasetID"

  private fun ReconciliationState.logInfo(message: String, vararg objects: Any?) =
    logger.info("$datasetRef: $message", *objects)

  private fun ReconciliationState.logWarning(message: String, vararg objects: Any?) {
    Metrics.ReconciliationHandler.warnings.inc()
    logger.warn("$datasetRef: $message", *objects)
  }

  private fun ReconciliationState.logError(message: String, error: Throwable? = null) {
    Metrics.ReconciliationHandler.errors.inc()
    if (error == null)
      logger.error("$datasetRef: $message")
    else
      logger.error("$datasetRef: $message", error)
  }
}

private class CriticalReconciliationError : Exception()

private class SyncIndicator(
  val metaOutOfSync: Boolean,
  val sharesOutOfSync: Boolean,
  val installOutOfSync: Boolean,
)

private class ReconciliationState(
  val datasetDirectory: DatasetDirectory,
  val source: EventSource,
) {
  inline val userID get() = datasetDirectory.ownerID
  inline val datasetID get() = datasetDirectory.datasetID

  var cMeta = ComputedValue<VDIDatasetMeta>()
  val cManifest = ComputedValue<VDIDatasetManifest>()

  val cHasDeleteFlag = ComputedFlag()
  val cHasRawUpload = ComputedFlag()
  val cHasImportable = ComputedFlag()
  val cHasInstallable = ComputedFlag()

  val cImportControl = ComputedValue<DatasetImportStatus>()
  val cImportMessages = ComputedFlag()

  var haveFiredMetaUpdateEvent = false
  var haveFiredImportEvent = false
  var haveFiredInstallEvent = false
  var haveFiredShareEvent = false
  var haveFiredUninstallEvent = false

  inline val haveFiredAnyEvents
    get() = haveFiredMetaUpdateEvent
      || haveFiredImportEvent
      || haveFiredInstallEvent
      || haveFiredShareEvent
      || haveFiredUninstallEvent
}

private class ComputedFlag {
  private var actual = false

  var value: Boolean
    get() {
      if (!hasBeenComputed)
        throw IllegalStateException("attempted to access computed flag before it was computed")
      return actual
    }
    set(value) {
      actual = value
      hasBeenComputed = true
    }

  var hasBeenComputed = false
    private set

  inline fun computeIfAbsent(fn: () -> Boolean): Boolean {
    if (!hasBeenComputed)
      value = fn()

    return value
  }
}

private class ComputedValue<T> {
  private var actual: T? = null

  var value: T?
    get() {
      if (!hasBeenComputed)
        throw IllegalStateException("attempted to access computed value before it was computed")
      return actual
    }
    set(value) {
      actual = value
      hasBeenComputed = true
    }

  var hasBeenComputed = false
    private set

  inline fun computeIfAbsent(fn: () -> T?): T? {
    if (!hasBeenComputed)
      value = fn()

    return value
  }
}
