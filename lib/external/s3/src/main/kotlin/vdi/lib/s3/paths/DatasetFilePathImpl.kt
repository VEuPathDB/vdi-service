package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

internal data class DatasetFilePathImpl(
  override val bucketName: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val file: S3File,
) : DatasetFilePath {
  override val isMetaFile
    get() = file == S3File.Metadata

  override val isManifestFile
    get() = file == S3File.Manifest

  override val isRawUploadFile
    get() = file == S3File.RawUploadZip

  override val isImportReadyFile
    get() = file == S3File.ImportReadyZip

  override val isInstallReadyFile
    get() = file == S3File.InstallReadyZip

  override val isDeleteFlagFile
    get() = file == S3File.DeleteFlag

  override fun toString() = "$bucketName/$userID/$datasetID/${file.baseName}"
}
