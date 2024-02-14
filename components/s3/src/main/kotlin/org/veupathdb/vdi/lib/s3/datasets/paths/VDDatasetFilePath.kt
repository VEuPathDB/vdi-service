package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

sealed interface VDDatasetFilePath : VDPath {
  val fileName: String

  val isMetaFile: Boolean
  val isManifestFile: Boolean
  val isRawUploadFile: Boolean
  val isImportReadyFile: Boolean
  val isInstallReadyFile: Boolean
  val isDeleteFlagFile: Boolean
}

internal data class VDDatasetFilePathImpl(
  override val bucketName: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val fileName: String
) : VDDatasetFilePath {
  override val isMetaFile
    get() = fileName == S3Paths.MetadataFileName

  override val isManifestFile
    get() = fileName == S3Paths.ManifestFileName

  override val isRawUploadFile
    get() = fileName == S3Paths.RawUploadZipName

  override val isImportReadyFile
    get() = fileName == S3Paths.ImportReadyZipName

  override val isInstallReadyFile
    get() = fileName == S3Paths.InstallReadyZipName

  override val isDeleteFlagFile
    get() = fileName == S3Paths.DeleteFlagFileName

  override fun toString() = "$bucketName/$userID/$datasetID/$fileName"
}