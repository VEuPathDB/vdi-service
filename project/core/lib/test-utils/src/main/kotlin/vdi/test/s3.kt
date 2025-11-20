package vdi.test

import org.mockito.kotlin.*
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.model.meta.DatasetManifest
import vdi.model.meta.DatasetMetadata
import java.io.InputStream
import java.time.OffsetDateTime
import java.util.stream.Stream
import vdi.core.s3.DatasetObjectStore
import vdi.core.s3.files.*
import vdi.core.s3.files.data.ImportReadyFile
import vdi.core.s3.files.data.InstallReadyFile
import vdi.core.s3.files.data.RawUploadFile
import vdi.core.s3.files.flags.DeleteFlagFile
import vdi.core.s3.files.meta.ManifestFile
import vdi.core.s3.files.meta.MetadataFile
import vdi.core.s3.files.shares.ShareOffer
import vdi.core.s3.files.shares.ShareReceipt


fun mockDatasetDirectory(
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,

  exists: Boolean = false,

  metaFile: MetadataFile = DummyMetaFile,
  hasMetaFile: Boolean = metaFile.exists(),
  onPutMetaFile: Con<DatasetMetadata> = ::consumer,
  onDeleteMetaFile: Runnable = ::runnable,

  manifest: ManifestFile = DummyManifest,
  hasManifest: Boolean = manifest.exists(),
  onPutManifest: Con<DatasetManifest> = ::consumer,
  onDeleteManifest: Runnable = ::runnable,

  deleteFlag: DeleteFlagFile = DummyDatasetFile,
  hasDeleteFlag: Boolean = deleteFlag.exists(),
  onPutDeleteFlag: Runnable = ::runnable,
  onDeleteDeleteFlag: Runnable = ::runnable,

  uploadFile: RawUploadFile = DummyDatasetFile,
  hasUploadFile: Boolean = uploadFile.exists(),
  onPutUploadFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteUploadFile: Runnable = ::runnable,

  importableFile: ImportReadyFile = DummyDatasetFile,
  hasImportableFile: Boolean = importableFile.exists(),
  onPutImportableFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteImportableFile: Runnable = ::runnable,

  installableFile: InstallReadyFile = DummyDatasetFile,
  hasInstallableFile: Boolean = installableFile.exists(),
  onPutInstallableFile: Con<Pro<InputStream>> = ::consumer,
  onDeleteInstallableFile: Runnable = ::runnable,

  shares: Map<UserID, Pair<ShareOffer, ShareReceipt>> = emptyMap(),
  onPutShare: Con<UserID> = ::consumer,
): vdi.core.s3.DatasetDirectory =
  mock {
    ownerID?.also { on { this.ownerID } doReturn it }
    datasetID?.also { on { this.datasetID } doReturn it }

    on { exists() } doReturn exists

    on { hasMetaFile() } doReturn hasMetaFile
    on { getMetaFile() } doReturn metaFile
    on { putMetaFile(any()) } doAnswer { onPutMetaFile(it.getArgument(0)) }
    on { deleteMetaFile() } doAnswer { onDeleteMetaFile() }

    on { hasManifestFile() } doReturn hasManifest
    on { getManifestFile() } doReturn manifest
    on { putManifestFile(any()) } doAnswer { onPutManifest(it.getArgument(0)) }
    on { deleteManifestFile() } doAnswer { onDeleteManifest() }

    on { hasDeleteFlag() } doReturn hasDeleteFlag
    on { getDeleteFlag() } doReturn deleteFlag
    on { putDeleteFlag() } doAnswer { onPutDeleteFlag() }
    on { deleteDeleteFlag() } doAnswer { onDeleteDeleteFlag() }

    on { hasUploadFile() } doReturn hasUploadFile
    on { getUploadFile() } doReturn uploadFile
    on { putUploadFile(any()) } doAnswer { onPutUploadFile(it.getArgument(0)) }
    on { deleteUploadFile() } doAnswer { onDeleteUploadFile() }

    on { hasImportReadyFile() } doReturn hasImportableFile
    on { getImportReadyFile() } doReturn importableFile
    on { putImportReadyFile(any()) } doAnswer { onPutImportableFile(it.getArgument(0)) }
    on { deleteImportReadyFile() } doAnswer { onDeleteImportableFile() }

    on { hasInstallReadyFile() } doReturn hasInstallableFile
    on { getInstallReadyFile() } doReturn installableFile
    on { putInstallReadyFile(any()) } doAnswer { onPutInstallableFile(it.getArgument(0))}
    on { deleteInstallReadyFile() } doAnswer { onDeleteInstallableFile() }

    on { getShares() } doReturn shares
    on { putShare(any()) } doAnswer { onPutShare(it.getArgument(0)) }
  }

fun mockDatasetManager(
  onGetDatasetDirectory: BiFn<UserID, DatasetID, vdi.core.s3.DatasetDirectory> = { u, d -> mockDatasetDirectory(ownerID = u, datasetID = d) },
  onListDatasets: Fn<UserID, List<DatasetID>> = ::oneParamList,
  onStreamAllDatasets: Pro<Stream<vdi.core.s3.DatasetDirectory>> = { Stream.empty() },
  onListUsers: Pro<List<UserID>> = ::noParamList,
): DatasetObjectStore =
  mock {
    on { getDatasetDirectory(any(), any()) } doAnswer { onGetDatasetDirectory(it.getArgument(0), it.getArgument(1)) }
    on { listDatasets(any()) } doAnswer { onListDatasets(it.getArgument(0)) }
    on { streamAllDatasets() } doAnswer { onStreamAllDatasets() }
    on { listUsers() } doAnswer { onListUsers() }
  }

fun mockImportableFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): ImportReadyFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockInstallableFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
): InstallReadyFile =
  mock { mockDatasetFile(path, baseName, exists, lastModified, contents) }

fun mockManifestFile(
  path: String? = null,
  baseName: String? = path?.substring(path.lastIndexOf('/') + 1),
  exists: Boolean = false,
  lastModified: OffsetDateTime? = null,
  contents: InputStream? = null,
  manifest: DatasetManifest? = null,
): ManifestFile =
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
  meta: DatasetMetadata? = null,
): MetadataFile =
  mock {
    mockDatasetFile(path, baseName, exists, lastModified, contents)
    on { load() } doReturn meta
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
  contents?.also { on { this.open() } doReturn it }
}
