package vdi.test

import org.mockito.kotlin.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareOffer
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import vdi.component.s3.DatasetManager
import vdi.component.s3.files.*
import java.io.InputStream
import java.time.OffsetDateTime
import java.util.stream.Stream

private sealed class DummyFileBase
  : DatasetDeleteFlagFile
  , DatasetRawUploadFile
  , DatasetImportableFile
  , DatasetInstallableFile
{
  override val path: String
    get() = throw Exception("not mocked")
  override fun loadContents() = null
  override fun exists() = false
  override fun lastModified() = null
}

private object DummyDatasetFile : DummyFileBase()

private object DummyMetaFile : DatasetMetaFile, DummyFileBase() {
  override fun load() = null
}

private object DummyManifest : DatasetManifestFile, DummyFileBase() {
  override fun load() = null
}

fun mockDatasetDirectory(
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,

  exists: Boolean = false,

  metaFile: DatasetMetaFile = DummyMetaFile,
  hasMetaFile: Boolean = metaFile.exists(),
  onPutMetaFile: Con<VDIDatasetMeta> = ::consumer,
  onDeleteMetaFile: Runnable = ::runnable,
  metaTimestamp: OffsetDateTime? = metaFile.lastModified(),

  manifest: DatasetManifestFile = DummyManifest,
  hasManifest: Boolean = manifest.exists(),
  onPutManifest: Con<VDIDatasetManifest> = ::consumer,
  onDeleteManifest: Runnable = ::runnable,
  manifestTimestamp: OffsetDateTime? = manifest.lastModified(),

  deleteFlag: DatasetDeleteFlagFile = DummyDatasetFile,
  hasDeleteFlag: Boolean = deleteFlag.exists(),
  onPutDeleteFlag: Runnable = ::runnable,
  onDeleteDeleteFlag: Runnable = ::runnable,
  deleteFlagTimestamp: OffsetDateTime? = deleteFlag.lastModified(),

  uploadFile: DatasetRawUploadFile = DummyDatasetFile,
  hasUploadFile: Boolean = uploadFile.exists(),
  onPutUploadFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteUploadFile: Runnable = ::runnable,
  uploadTimestamp: OffsetDateTime? = uploadFile.lastModified(),

  importableFile: DatasetImportableFile = DummyDatasetFile,
  hasImportableFile: Boolean = importableFile.exists(),
  onPutImportableFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteImportableFile: Runnable = ::runnable,
  importableTimestamp: OffsetDateTime? = importableFile.lastModified(),

  installableFile: DatasetInstallableFile = DummyDatasetFile,
  hasInstallableFile: Boolean = installableFile.exists(),
  onPutInstallableFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteInstallableFile: Runnable = ::runnable,
  installableTimestamp: OffsetDateTime? = installableFile.lastModified(),

  shares: Map<UserID, DatasetShare> = emptyMap(),
  onPutShare: Con<UserID> = ::consumer,

  onGetLatestShareTimestamp: (OffsetDateTime) -> OffsetDateTime = { it },
): vdi.component.s3.DatasetDirectory =
  mock {
    ownerID?.also { on { this.ownerID } doReturn it }
    datasetID?.also { on { this.datasetID } doReturn it }

    on { exists() } doReturn exists

    on { hasMetaFile() } doReturn hasMetaFile
    on { getMetaFile() } doReturn metaFile
    on { putMetaFile(any()) } doAnswer { onPutMetaFile(it.getArgument(0)) }
    on { deleteMetaFile() } doAnswer { onDeleteMetaFile() }
    on { getMetaTimestamp() } doReturn metaTimestamp

    on { hasManifestFile() } doReturn hasManifest
    on { getManifestFile() } doReturn manifest
    on { putManifestFile(any()) } doAnswer { onPutManifest(it.getArgument(0)) }
    on { deleteManifestFile() } doAnswer { onDeleteManifest() }
    on { getManifestTimestamp() } doReturn manifestTimestamp

    on { hasDeleteFlag() } doReturn hasDeleteFlag
    on { getDeleteFlag() } doReturn deleteFlag
    on { putDeleteFlag() } doAnswer { onPutDeleteFlag() }
    on { deleteDeleteFlag() } doAnswer { onDeleteDeleteFlag() }
    on { getDeleteFlagTimestamp() } doReturn deleteFlagTimestamp

    on { hasUploadFile() } doReturn hasUploadFile
    on { getUploadFile() } doReturn uploadFile
    on { putUploadFile(any()) } doAnswer { onPutUploadFile(it.getArgument(0)) }
    on { deleteUploadFile() } doAnswer { onDeleteUploadFile() }
    on { getUploadTimestamp() } doReturn uploadTimestamp

    on { hasImportReadyFile() } doReturn hasImportableFile
    on { getImportReadyFile() } doReturn importableFile
    on { putImportReadyFile(any()) } doAnswer { onPutImportableFile(it.getArgument(0)) }
    on { deleteImportReadyFile() } doAnswer { onDeleteImportableFile() }
    on { getImportReadyTimestamp() } doReturn importableTimestamp

    on { hasInstallReadyFile() } doReturn hasInstallableFile
    on { getInstallReadyFile() } doReturn installableFile
    on { putInstallReadyFile(any()) } doAnswer { onPutInstallableFile(it.getArgument(0))}
    on { deleteInstallReadyFile() } doAnswer { onDeleteInstallableFile() }
    on { getInstallReadyTimestamp() } doReturn installableTimestamp

    on { getShares() } doReturn shares
    on { putShare(any()) } doAnswer { onPutShare(it.getArgument(0)) }

    on { getLatestShareTimestamp(any()) } doAnswer { onGetLatestShareTimestamp(it.getArgument(0)) }
  }

fun mockDatasetManager(
  onGetDatasetDirectory: BiFn<UserID, DatasetID, vdi.component.s3.DatasetDirectory> = { u, d -> mockDatasetDirectory(ownerID = u, datasetID = d) },
  onListDatasets: Fn<UserID, List<DatasetID>> = ::oneParamList,
  onStreamAllDatasets: Pro<Stream<vdi.component.s3.DatasetDirectory>> = { Stream.empty() },
  onListUsers: Pro<List<UserID>> = ::noParamList,
): DatasetManager =
  mock {
    on { getDatasetDirectory(any(), any()) } doAnswer { onGetDatasetDirectory(it.getArgument(0), it.getArgument(1)) }
    on { listDatasets(any()) } doAnswer { onListDatasets(it.getArgument(0)) }
    on { streamAllDatasets() } doAnswer { onStreamAllDatasets() }
    on { listUsers() } doAnswer { onListUsers() }
  }

fun mockDeleteFlagFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): DatasetDeleteFlagFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockImportableFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): DatasetImportableFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockInstallableFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): DatasetInstallableFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockManifestFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
  manifest: VDIDatasetManifest? = null,
): DatasetManifestFile =
  mock {
    mockDatasetFile(path, baseName, exists, lastModified, contents)
    on { load() } doReturn manifest
  }

fun mockMetaFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
  meta: VDIDatasetMeta? = null,
): DatasetMetaFile =
  mock {
    mockDatasetFile(path, baseName, exists, lastModified, contents)
    on { load() } doReturn meta
  }

fun mockRawUploadFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): DatasetRawUploadFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockDatasetShare(
  recipientID: UserID? = null,
  offer: DatasetShareOfferFile? = null,
  receipt: DatasetShareReceiptFile? = null,
): DatasetShare =
  mock {
    recipientID?.also { on { this.recipientID } doReturn it }
    offer?.also { on { this.offer } doReturn it }
    receipt?.also { on { this.receipt } doReturn it }
  }

fun mockShareOfferFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
  offer: VDIDatasetShareOffer? = null,
): DatasetShareOfferFile =
  mock {
    mockDatasetFile(path, baseName, exists, lastModified, contents)
    on { load() } doReturn offer
  }

fun mockShareReceiptFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
  receipt: VDIDatasetShareReceipt? = null
): DatasetShareReceiptFile =
  mock {
    mockDatasetFile(path, baseName, exists, lastModified, contents)
    on { load() } doReturn receipt
  }

private fun KStubbing<DatasetFile>.mockDatasetFile(
  path: String?,
  baseName: String?,
  exists: Boolean,
  lastModified: OffsetDateTime?,
  contents: InputStream?,
) {
  path?.also { on { this.path } doReturn it }
  baseName?.also { on { this.baseName } doReturn it }
  on { this.exists() } doReturn exists
  lastModified?.also { on { this.lastModified() } doReturn it }
  contents?.also { on { this.loadContents() } doReturn it }
}