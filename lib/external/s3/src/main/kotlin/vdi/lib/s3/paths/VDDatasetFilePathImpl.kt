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
    get() = fileName == S3File.Metadata

  override val isManifestFile
    get() = fileName == S3File.Manifest

  override val isRawUploadFile
    get() = fileName == S3File.RawUploadZip

  override val isImportReadyFile
    get() = fileName == S3File.ImportReadyZip

  override val isInstallReadyFile
    get() = fileName == S3File.InstallReadyZip

  override val isDeleteFlagFile
    get() = fileName == S3File.DeleteFlag

  override fun toString() = "$bucketName/$userID/$datasetID/$fileName"
}
