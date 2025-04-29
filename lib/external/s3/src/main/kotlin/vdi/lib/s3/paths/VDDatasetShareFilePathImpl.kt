package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

internal data class VDDatasetShareFilePathImpl(
  override val bucketName: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val recipientID: UserID,
  override val fileName: String
) : VDDatasetShareFilePath {
  override val isOffer
    get() = fileName == S3File.ShareOffer

  override val isReceipt
    get() = fileName == S3File.ShareReceipt

  override fun toString() =
    "$bucketName/$userID/$datasetID/${S3File.SharesDir}/$recipientID/$fileName"
}
