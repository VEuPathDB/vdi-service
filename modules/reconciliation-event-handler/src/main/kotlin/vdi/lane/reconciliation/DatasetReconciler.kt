package vdi.lane.reconciliation

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDBAccessor
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetImpl
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl
import org.veupathdb.vdi.lib.db.cache.withTransaction
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import vdi.component.metrics.Metrics
import java.time.OffsetDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord as CacheDatasetRecord

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

  fun reconcile(userID: UserID, datasetID: DatasetID) {
    logger.info("beginning reconciliation for dataset {}/{}", userID, datasetID)
    try {
      ReconciliationState(datasetManager.getDatasetDirectory(userID, datasetID)).reconcile()
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

    if (shouldReimport() == ReimportIndicator.NeedReimport) {
      tryReimport()
      return
    }

    if (!haveImportableFile())
      logError("$userID/$datasetID: missing import-ready file")

    if (!haveInstallableFile())
      logError("$userID/$datasetID: missing install-ready file")

    if (!haveManifestFile())
      logError("$userID/$datasetID: missing manifest file")

    if (haveInstallableFile())
      runSync(getProjects())
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
    // If an import-ready.zip file exists for the dataset then we can delete
    // the raw upload file and ensure that the upload control table in the
    // cache db indicates that the upload was successfully processed.
    if (haveImportableFile()) {
      safeExec("failed to delete raw upload file") { datasetDirectory.deleteUploadFile() }
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

  private fun ReconciliationState.runSync(projects: Iterable<ProjectID>) {
    val syncStatus = checkSyncStatus(projects)

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

  private fun ReconciliationState.checkSyncStatus(projects: Iterable<ProjectID>): SyncIndicator {
    val cacheDBSyncControl = requireCacheDBSyncControl()

    val metaTimestamp = datasetDirectory.getMetaTimestamp() ?: cacheDBSyncControl.metaUpdated
    val latestShareTimestamp = datasetDirectory.getLatestShareTimestamp(cacheDBSyncControl.sharesUpdated)
    val installDataTimestamp = datasetDirectory.getInstallReadyTimestamp() ?: cacheDBSyncControl.dataUpdated

    var metaOutOfSync = cacheDBSyncControl.metaUpdated.isBefore(metaTimestamp)
    var sharesOutOfSync = cacheDBSyncControl.sharesUpdated.isBefore(latestShareTimestamp)
    var installOutOfSync = cacheDBSyncControl.dataUpdated.isBefore(installDataTimestamp)

    if (metaOutOfSync && sharesOutOfSync && installOutOfSync)
      return SyncIndicator(metaOutOfSync = true, sharesOutOfSync = true, installOutOfSync = true)

    projects.forEach { projectID ->
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
    fireImportEvent()
    haveFiredImportEvent = true
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
    ReimportNotPossible,
    NeedReimport,
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
      eventRouter.sendImportTrigger(userID, datasetID)
    }
  }

  private fun ReconciliationState.fireUpdateMetaEvent() {
    logger.info("firing update meta event for dataset {}/{}", userID, datasetID)
    safeExec("failed to fire update-meta trigger") {
      eventRouter.sendUpdateMetaTrigger(userID, datasetID)
    }
  }

  private fun ReconciliationState.fireShareEvent() {
    logger.info("firing share event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send share trigger") {
      eventRouter.sendShareTrigger(userID, datasetID)
    }
  }

  private fun ReconciliationState.fireInstallEvent() {
    logger.info("firing data install event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send install-data trigger") {
      eventRouter.sendInstallTrigger(userID, datasetID)
    }
  }

  private fun ReconciliationState.fireUninstallEvent() {
    logger.info("firing soft-delete/uninstall event for dataset {}/{}", userID, datasetID)
    safeExec("failed to send soft-delete trigger") {
      eventRouter.sendSoftDeleteTrigger(userID, datasetID)
    }
  }

  // endregion Kafka

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

    if (targetRecord == null) {
      logWarning("attempted to check install status for dataset {}/{} in project {} but no such dataset record could be found", userID, datasetID, project)
      return false
    }

    if (targetRecord.isDeleted != DeleteFlag.DeletedAndUninstalled) {
      return false
    }

    return true
  }

  // region CacheDB
  // // // // // // // // // // // // // // // // // // // // // // // // // //
  //
  //     Cache DB Operations
  //
  // // // // // // // // // // // // // // // // // // // // // // // // // //

  private fun ReconciliationState.failedImport() =
    getCacheImportControl() == DatasetImportStatus.Failed

  private fun ReconciliationState.missingImportMessage() =
    safeExec("failed to fetch import messages from cache db") { cacheDB.selectImportMessages(datasetID).isEmpty() }

  private fun ReconciliationState.getCacheImportControl() =
    safeExec("failed to fetch import control from cache db") { cacheDB.selectImportControl(datasetID) }

  private fun ReconciliationState.updateCacheDBImportStatus(status: DatasetImportStatus) {
    try {
      cacheDB.withTransaction { it.upsertImportControl(datasetID, status) }
    } catch (e: Throwable) {
      logError("failed to update dataset import status to $status for dataset $userID/$datasetID", e)
    }
  }

  private fun ReconciliationState.getCacheDatasetRecord() =
    cCacheDBDatasetRecord.computeIfAbsent {
      safeExec("failed to query cache db for dataset record") { cacheDB.selectDataset(datasetID) }
    }

  private fun ReconciliationState.requireCacheDatasetRecord() =
    getCacheDatasetRecord().require("could not find dataset record in cache db")

  private fun ReconciliationState.requireCacheDBSyncControl() =
    safeExec("failed to load sync control record from cache db") {
      cacheDB.selectSyncControl(datasetID)
    }.require("could not find dataset sync control record")

  private fun ReconciliationState.tryInitCacheDB() {
    val needRoot = getCacheDatasetRecord() == null
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
        if (needRoot)
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

        manifest.ifNotNull {
          db.tryInsertUploadFiles(datasetID, it.inputFiles)
          db.tryInsertInstallFiles(datasetID, it.dataFiles)
        }
      }
    }

    safeExec("failed to select dataset record from cache db") {
      cCacheDBDatasetRecord.value = cacheDB.selectDataset(datasetID)
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

private class CriticalReconciliationError : Exception()

private class SyncIndicator(
  val metaOutOfSync: Boolean,
  val sharesOutOfSync: Boolean,
  val installOutOfSync: Boolean,
)

private class ReconciliationState(val datasetDirectory: DatasetDirectory) {
  inline val userID get() = datasetDirectory.ownerID
  inline val datasetID get() = datasetDirectory.datasetID

  var cMeta = ComputedValue<VDIDatasetMeta>()
  val cManifest = ComputedValue<VDIDatasetManifest>()

  val cHasDeleteFlag = ComputedFlag()
  val cHasRawUpload = ComputedFlag()
  val cHasImportable = ComputedFlag()
  val cHasInstallable = ComputedFlag()

  var haveFiredImportEvent = false

  val cCacheDBDatasetRecord = ComputedValue<CacheDatasetRecord>()
}

class ComputedFlag {
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

class ComputedValue<T> {
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

// TODO: Move this to commons
@OptIn(ExperimentalContracts::class)
private inline fun <T> T?.ifNull(fn: () -> Unit): T? {
  contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }

  if (this == null)
    fn()

  return this
}

@OptIn(ExperimentalContracts::class)
private inline fun <T> T?.ifNotNull(fn: (T) -> Unit): T? {
  contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }

  if (this != null)
    fn(this)

  return this
}