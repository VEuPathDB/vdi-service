package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

sealed interface VDDatasetFilePath : VDPath {
  val subPath: String
}

internal data class VDDatasetFilePathImpl(
  override val bucketName: String,
  override val rootSegment: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val subPath: String
) : VDDatasetFilePath {
  override fun toString() =
    "$bucketName/$rootSegment/$userID/$datasetID/${S3Paths.DATASET_DIR_NAME}/$subPath"
}