package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

internal data class VDDatasetFilePathImpl(
  override val bucketName: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val fileName: String
) : VDDatasetFilePath {
  override val isMetaFile
    get() = fileName == S3File.MetadataFileName

  override val isManifestFile
    get() = fileName == S3File.ManifestFileName

  override val isRawUploadFile
    get() = fileName == S3File.RawUploadZipName

  override val isImportReadyFile
    get() = fileName == S3File.ImportReadyZipName

  override val isInstallReadyFile
    get() = fileName == S3File.InstallReadyZipName

  override val isDeleteFlagFile
    get() = fileName == S3File.DeleteFlagFileName

  override fun toString() = "$bucketName/$userID/$datasetID/$fileName"
}
