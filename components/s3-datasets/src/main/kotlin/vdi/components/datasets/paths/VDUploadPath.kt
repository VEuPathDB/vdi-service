package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

sealed interface VDUploadPath : VDPath {
  val subPath: String
}

data class VDUploadPathImpl(
  override val bucketName: String,
  override val rootSegment: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val subPath: String
) : VDUploadPath {
  override fun toString() = "$bucketName/$rootSegment/$userID/$datasetID/${S3Paths.UPLOAD_DIR_NAME}/$subPath"
}