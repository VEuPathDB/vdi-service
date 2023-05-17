package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

sealed interface VDDatasetShareFilePath : VDPath {
  val recipientID: UserID
  val subPath: String
}

internal data class VDDatasetShareFilePathImpl(
  override val bucketName: String,
  override val rootSegment: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val recipientID: UserID,
  override val subPath: String
) : VDDatasetShareFilePath {
  override fun toString() =
    "$bucketName/$rootSegment/$userID/$datasetID/${S3Paths.DATASET_DIR_NAME}/${S3Paths.SHARES_DIR_NAME}/$recipientID/$subPath"
}