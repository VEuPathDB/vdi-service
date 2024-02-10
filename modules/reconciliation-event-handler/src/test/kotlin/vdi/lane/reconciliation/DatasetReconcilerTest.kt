package vdi.lane.reconciliation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.internal.matchers.CapturingMatcher
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDBAccessor
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.files.*
import java.time.OffsetDateTime
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord as CacheDatasetRecord
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord as AppDatasetRecord

class DatasetReconcilerTest {

  @Nested
  @DisplayName("meta file missing from data store")
  inner class NoMeta {

    @Test
    @DisplayName("with valid cache db record")
    fun test0() {}

    @Test
    @DisplayName("with invalid cache db record")
    fun test1() {}

    @Test
    @DisplayName("with no cache db record")
    fun test2() {}
  }
}

private fun mockAppDBAccessor(
  datasetRecord: AppDatasetRecord? = null,
  syncControl: VDISyncControlRecord? = null,
): AppDBAccessor =
  mock(AppDBAccessor::class.java).also {
    `when`(it.selectDataset(any())).thenReturn(datasetRecord)
    `when`(it.selectDatasetSyncControlRecord(any())).thenReturn(syncControl)
  }

private fun mockAppDB(
  accessors: Map<ProjectID, AppDBAccessor> = emptyMap(),
): AppDB =
  mock(AppDB::class.java).also {
    val capture = CapturingMatcher(String::class.java)
    `when`(it.accessor(argThat(capture))).thenReturn(accessors[capture.lastValue])
  }

private fun mockCacheDB(
  datasetRecord: CacheDatasetRecord? = null,
  importControl: DatasetImportStatus? = null,
  syncControl: VDISyncControlRecord? = null,
): CacheDB =
  mock(CacheDB::class.java).also {
    `when`(it.selectDataset(any())).thenReturn(datasetRecord)
    `when`(it.selectImportControl(any())).thenReturn(importControl)
    `when`(it.selectSyncControl(any())).thenReturn(syncControl)
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
  mock(DatasetDirectory::class.java).also {
    `when`(it.exists()).thenReturn(exists)

    `when`(it.hasMetaFile()).thenReturn(hasMetaFile)
    `when`(it.getMetaFile()).thenReturn(metaFile)
    `when`(it.getMetaTimestamp()).thenReturn(metaFileTimestamp)

    `when`(it.hasManifestFile()).thenReturn(hasManifest)
    `when`(it.getManifestFile()).thenReturn(manifest)
    `when`(it.getManifestTimestamp()).thenReturn(manifestTimestamp)

    `when`(it.hasDeleteFlag()).thenReturn(hasDeleteFlag)
    `when`(it.getDeleteFlag()).thenReturn(deleteFlag)
    `when`(it.getDeleteFlagTimestamp()).thenReturn(deleteFlagTimestamp)

    `when`(it.hasUploadFile()).thenReturn(hasUploadFile)
    `when`(it.getUploadFile()).thenReturn(uploadFile)
    `when`(it.getUploadTimestamp()).thenReturn(uploadTimestamp)

    `when`(it.hasImportReadyFile()).thenReturn(hasImportReadyFile)
    `when`(it.getImportReadyFile()).thenReturn(importReadyFile)
    `when`(it.getImportReadyTimestamp()).thenReturn(importReadyTimestamp)

    `when`(it.hasInstallReadyFile()).thenReturn(hasInstallReadyFile)
    `when`(it.getInstallReadyFile()).thenReturn(installReadyFile)
    `when`(it.getInstallReadyTimestamp()).thenReturn(installReadyTimestamp)

    `when`(it.getLatestShareTimestamp(any())).thenReturn(latestShareTimestamp)
  }

private fun mockDatasetManager(dir: DatasetDirectory): DatasetManager =
  mock(DatasetManager::class.java).also {
    val userIDMatcher = CapturingMatcher(UserID::class.java)
    val datasetIDMatcher = CapturingMatcher(DatasetID::class.java)
    `when`(it.getDatasetDirectory(argThat(userIDMatcher), argThat(datasetIDMatcher))).thenReturn(dir)
    `when`(dir.ownerID).thenReturn(userIDMatcher.lastValue)
    `when`(dir.datasetID).thenReturn(datasetIDMatcher.lastValue)
  }