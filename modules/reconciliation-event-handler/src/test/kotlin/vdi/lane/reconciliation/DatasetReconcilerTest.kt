package vdi.lane.reconciliation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.internal.matchers.CapturingMatcher
import org.mockito.kotlin.*
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDBAccessor
import org.veupathdb.vdi.lib.db.app.model.DeleteFlag
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.files.*
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.random.Random
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord as CacheDatasetRecord
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord as AppDatasetRecord

private val userID = UserID(123456)

private val datasetID = DatasetID()

class DatasetReconcilerTest {

  // has delete flag and is not fully uninstalled -> fire soft-delete event
  // has delete flag and is fully uninstalled     -> do nothing
  @Nested
  @DisplayName("delete flag is present")
  inner class HasDeleteFlag {

    @Test
    @DisplayName("has no cache-db record")
    fun test0() {
      val projects = listOf("foo")
      val cacheDB = mockCacheDB()
      val accessor = mockAppDBAccessor(datasetRecord = makeAppDatasetRecord())
      val appDB = mockAppDB(accessors = mapOf(projects[0] to accessor))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(cacheDB, appDB, router, datasetManager).reconcile(userID, datasetID)

      // The deletion path should attempt to load the dataset from the cache
      // db, if the dataset is not found in the cache db then no further
      // operations should be performed on it.
      verify(cacheDB, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(cacheDB)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // The deletion path should attempt to load the dataset from the target
      // app db to check its deletion status.
      verify(accessor, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(accessor)

      // Because the target db dataset record is not marked as deleted, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }

    @Test
    @DisplayName("has cache-db record, not marked as deleted")
    fun test1() {
      val projects = listOf("foo")
      val cacheTransaction = mockCacheTransaction()
      val cacheDB = mockCacheDB(
        datasetRecord = mockCacheDatasetRecord(projects = projects, isDeleted = false),
        transaction = cacheTransaction,
      )
      val accessor = mockAppDBAccessor(datasetRecord = makeAppDatasetRecord())
      val appDB = mockAppDB(accessors = mapOf(projects[0] to accessor))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(cacheDB, appDB, router, datasetManager).reconcile(userID, datasetID)

      // The deletion process should attempt to load the dataset record from the
      // cache db to test whether it is already marked as deleted.  Since our
      // test record has `isDeleted` set to false, the process should then
      // attempt to mark the record as deleted in the cache db.
      verify(cacheDB, times(1)).selectDataset(datasetID)
      verify(cacheDB, times(1)).openTransaction()
      verifyNoMoreInteractions(cacheDB)
      verify(cacheTransaction, times(1)).updateDatasetDeleted(datasetID, true)
      verify(cacheTransaction, times(1)).commit()
      verify(cacheTransaction, times(1)).close()
      verifyNoMoreInteractions(cacheTransaction)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // The deletion path should attempt to load the dataset from the target
      // app db to check its deletion status.
      verify(accessor, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(accessor)

      // Because the target db dataset record is not marked as deleted, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }

    @Test
    @DisplayName("has cache-db record, marked as deleted")
    fun test2() {
      val projects = listOf("foo")
      val cacheDB = mockCacheDB(
        datasetRecord = mockCacheDatasetRecord(projects = projects, isDeleted = true),
      )
      val accessor = mockAppDBAccessor(datasetRecord = makeAppDatasetRecord())
      val appDB = mockAppDB(accessors = mapOf(projects[0] to accessor))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(cacheDB, appDB, router, datasetManager).reconcile(userID, datasetID)

      // The deletion process should attempt to load the dataset record from the
      // cache db to test whether it is already marked as deleted.  Since our
      // test record has `isDeleted` set to true, the process should take no
      // further action in the cache DB.
      verify(cacheDB, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(cacheDB)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // The deletion path should attempt to load the dataset from the target
      // app db to check its deletion status.
      verify(accessor, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(accessor)

      // Because the target db dataset record is not marked as deleted, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }

    @Test
    @DisplayName("targets a project that is disabled")
    fun test3() {
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects = listOf("foo")))
      ))

      makeReconciler(router = router, datasetManager = datasetManager).reconcile(userID, datasetID)

      // Because the target db dataset record is not marked as deleted, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }

    @Test
    @DisplayName("has no record in target app database")
    fun test4() {
      val projects = listOf("foo")
      val appDB = mockAppDB(accessors = mapOf(projects[0] to mockAppDBAccessor()))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(appDB = appDB, router = router, datasetManager = datasetManager).reconcile(userID, datasetID)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // Because the target db dataset record is not marked as deleted, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }

    @Test
    @DisplayName("has record in target db marked as deleted and uninstalled")
    fun test5() {
      val projects = listOf("foo")
      val accessor = mockAppDBAccessor(
        datasetRecord = makeAppDatasetRecord(deleteFlag = DeleteFlag.DeletedAndUninstalled)
      )
      val appDB = mockAppDB(accessors = mapOf(projects[0] to accessor))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(appDB = appDB, router = router, datasetManager = datasetManager).reconcile(userID, datasetID)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // The deletion path should attempt to load the dataset from the target
      // app db to check its deletion status.
      verify(accessor, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(accessor)

      // Because the target db dataset record is marked as deleted and
      // uninstalled no events should be fired.
      verifyNoInteractions(router)
    }

    // Has target record not marked as deleted and uninstalled
    @Test
    @DisplayName("is not marked as deleted and uninstalled in target database")
    fun test6() {
      val projects = listOf("foo")
      val accessor = mockAppDBAccessor(
        datasetRecord = makeAppDatasetRecord(deleteFlag = DeleteFlag.DeletedNotUninstalled)
      )
      val appDB = mockAppDB(accessors = mapOf(projects[0] to accessor))
      val router = mockEventRouter()
      val datasetManager = mockDatasetManager(mockDatasetDirectory(
        hasDeleteFlag = true,
        hasMetaFile = true,
        metaFile = mockMetaFile(mockMeta(projects))
      ))

      makeReconciler(appDB = appDB, router = router, datasetManager = datasetManager).reconcile(userID, datasetID)

      // The deletion path should attempt to get an accessor for the dataset's
      // target app db.
      verify(appDB, times(1)).accessor(projects[0])
      verifyNoMoreInteractions(appDB)

      // The deletion path should attempt to load the dataset from the target
      // app db to check its deletion status.
      verify(accessor, times(1)).selectDataset(datasetID)
      verifyNoMoreInteractions(accessor)

      // Because the target db dataset record is not marked as uninstalled, the
      // deletion path should fire an uninstall/soft-delete event.
      verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID)
      verifyNoMoreInteractions(router)
    }
  }

  @Nested
  @DisplayName("has raw upload file")
  inner class HasRawUpload {
    // TODO: has upload file and import ready file    -> delete upload file
    // TODO: has upload file and no import ready file -> fire upload event
  }

  @Nested
  @DisplayName("has import-ready zip")
  inner class HasImportReady {
    // has import ready zip
    //   has install ready AND manifest              -> update import status, sync cache db file tables
    //   has install ready OR manifest               -> fire import event
    //   has neither and import status is failed     -> do nothing
    //   has neither and import status is not failed -> fire import event

    @Test
    @DisplayName("has install-ready AND manifest")
    fun test0() {
      val projects = listOf("foo")
      val inputFiles = listOf(generateFileInfo())
      val outputFiles = listOf(generateFileInfo())
      val dsDir = mockDatasetDirectory(
        hasMetaFile = true,
        metaFile = mockMetaFile(meta = mockMeta(projects = projects)),
        metaFileTimestamp = OriginTimestamp,
        hasManifest = true,
        manifest = mockManifestFile(manifest = mockManifest(inputFiles = inputFiles, dataFiles = outputFiles)),
        manifestTimestamp = OriginTimestamp,
        hasInstallReadyFile = true,

      )
    }

  }


  // has install ready zip
  //   has no import ready zip but has manifest      -> do nothing
  //   has no import ready zip and has no manifest   -> do nothing
  //   has import ready zip and has fired import event -> do nothing
  //   has import ready zip and has not fired import event -> fire import event

  // has only manifest
  //   has no import ready zip -> sync cache db tables
  //   has import ready zip    -> fire import event, sync cache db tables

}

private fun makeReconciler(
  cacheDB: CacheDB = mockCacheDB(),
  appDB: AppDB = mockAppDB(),
  router: KafkaRouter = mockEventRouter(),
  datasetManager: DatasetManager,
) = DatasetReconciler(cacheDB, appDB, router, datasetManager)

private fun generateFileInfo(): VDIDatasetFileInfo =
  VDIDatasetFileInfoImpl(
    filename = UUID.randomUUID().toString(),
    fileSize = Random.nextLong(),
  )

private fun mockMeta(
  projects: Iterable<ProjectID> = emptyList()
): VDIDatasetMeta =
  mock {
    on { this.owner } doReturn userID
    on { this.projects } doReturn projects.toSet()
  }

private fun mockManifest(
  inputFiles: Collection<VDIDatasetFileInfo> = emptyList(),
  dataFiles: Collection<VDIDatasetFileInfo> = emptyList(),
): VDIDatasetManifest =
  mock {
    on { this.inputFiles } doReturn inputFiles
    on { this.dataFiles } doReturn dataFiles
  }

private fun mockMetaFile(meta: VDIDatasetMeta? = null): DatasetMetaFile =
  mock { on { load() } doReturn meta }

private fun mockManifestFile(manifest: VDIDatasetManifest? = null): DatasetManifestFile =
  mock { on { load() } doReturn manifest }

private fun makeAppDatasetRecord(deleteFlag: DeleteFlag = DeleteFlag.NotDeleted) =
  AppDatasetRecord(
    datasetID = datasetID,
    owner = userID,
    typeName = "",
    typeVersion = "",
    isDeleted = deleteFlag
  )

private fun mockCacheDatasetRecord(
  projects: Collection<ProjectID> = emptyList(),
  isDeleted: Boolean = false,
): CacheDatasetRecord =
  mock {
    on { this.datasetID } doReturn datasetID
    on { this.ownerID } doReturn userID
    on { this.isDeleted } doReturn isDeleted
    on { this.projects } doReturn projects
  }

private fun mockEventRouter(): KafkaRouter = mock()

private fun mockAppDBAccessor(
  datasetRecord: AppDatasetRecord? = null,
  syncControl: VDISyncControlRecord? = null,
): AppDBAccessor =
  mock {
    on { selectDataset(datasetID) } doReturn datasetRecord
    on { selectDatasetSyncControlRecord(datasetID) } doReturn syncControl
  }

private fun mockAppDB(
  accessors: Map<ProjectID, AppDBAccessor> = emptyMap(),
): AppDB =
  mock {
    on { accessor(any()) } doAnswer { accessors[it.getArgument(0)] }
  }

private fun mockCacheTransaction(): CacheDBTransaction = mock()

private fun mockCacheDB(
  datasetRecord: CacheDatasetRecord? = null,
  importControl: DatasetImportStatus? = null,
  syncControl: VDISyncControlRecord? = null,
  transaction: CacheDBTransaction? = null,
): CacheDB =
  mock {
    on { selectDataset(datasetID) } doReturn datasetRecord
    on { selectImportControl(datasetID) } doReturn importControl
    on { selectSyncControl(datasetID) } doReturn syncControl
    transaction?.also { on { openTransaction() } doReturn it }
  }

private fun mockDatasetDirectory(
  exists: Boolean = true,
  hasMetaFile: Boolean = false,
  metaFile: DatasetMetaFile? = null,
  metaFileTimestamp: OffsetDateTime? = null,
  hasManifest: Boolean = false,
  manifest: DatasetManifestFile? = null,
  manifestTimestamp: OffsetDateTime? = null,
  hasDeleteFlag: Boolean = false,
  deleteFlag: DatasetDeleteFlagFile? = null,
  deleteFlagTimestamp: OffsetDateTime? = null,
  hasUploadFile: Boolean = false,
  uploadFile: DatasetRawUploadFile? = null,
  uploadTimestamp: OffsetDateTime? = null,
  hasImportReadyFile: Boolean = false,
  importReadyFile: DatasetImportableFile? = null,
  importReadyTimestamp: OffsetDateTime? = null,
  hasInstallReadyFile: Boolean = false,
  installReadyFile: DatasetInstallableFile? = null,
  installReadyTimestamp: OffsetDateTime? = null,
  latestShareTimestamp: OffsetDateTime? = null,
): DatasetDirectory =
  mock {
    on { ownerID } doReturn userID
    on { datasetID } doReturn datasetID

    on { exists() } doReturn exists

    on { hasMetaFile() } doReturn hasMetaFile
    metaFile?.also { on { getMetaFile() } doReturn it }
    metaFileTimestamp?.also { on { getMetaTimestamp() } doReturn it }

    on { hasManifestFile() } doReturn hasManifest
    manifest?.also { on { getManifestFile() } doReturn it }
    manifestTimestamp?.also { on { getManifestTimestamp() } doReturn  it }

    on { hasDeleteFlag() } doReturn hasDeleteFlag
    deleteFlag?.also { on { getDeleteFlag() } doReturn it }
    deleteFlagTimestamp?.also { on { getDeleteFlagTimestamp() } doReturn it }

    on { hasUploadFile() } doReturn hasUploadFile
    uploadFile?.also { on { getUploadFile() } doReturn it }
    uploadTimestamp?.also { on { getUploadTimestamp() } doReturn it }

    on { hasImportReadyFile() } doReturn hasImportReadyFile
    importReadyFile?.also { on { getImportReadyFile() } doReturn it }
    importReadyTimestamp?.also { on { getImportReadyTimestamp() } doReturn it }

    on { hasInstallReadyFile() } doReturn hasInstallReadyFile
    installReadyFile?.also { on { getInstallReadyFile() } doReturn it }
    installReadyTimestamp?.also { on { getInstallReadyTimestamp() } doReturn it }

    latestShareTimestamp?.also { on { getLatestShareTimestamp(any()) } doReturn it }
  }

private fun mockDatasetManager(dir: DatasetDirectory = mockDatasetDirectory()): DatasetManager =
  mock { on { getDatasetDirectory(userID, datasetID) } doReturn dir }