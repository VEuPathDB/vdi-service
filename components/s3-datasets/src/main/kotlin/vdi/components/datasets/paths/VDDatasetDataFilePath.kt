package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

sealed interface VDDatasetDataFilePath : VDPath {
  val subPath: String
}

data class VDDatasetDataFilePathImpl(
  override val bucketName: String,
  override val rootSegment: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val subPath: String
) : VDDatasetDataFilePath {
  override fun toString() =
    "$bucketName/$rootSegment/$userID/$datasetID/${S3Paths.DATASET_DIR_NAME}/${S3Paths.DATA_DIR_NAME}/$subPath"
}