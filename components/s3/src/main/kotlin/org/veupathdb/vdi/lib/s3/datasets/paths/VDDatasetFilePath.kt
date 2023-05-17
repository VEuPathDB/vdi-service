package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

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