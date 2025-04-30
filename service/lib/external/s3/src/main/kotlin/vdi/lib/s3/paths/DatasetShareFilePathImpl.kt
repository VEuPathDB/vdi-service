package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

internal data class DatasetShareFilePathImpl(
  override val bucketName: String,
  override val userID: UserID,
  override val datasetID: DatasetID,
  override val recipientID: UserID,
  override val file: S3File,
) : DatasetShareFilePath {
  override val isOffer
    get() = file == S3File.ShareOffer

  override val isReceipt
    get() = file == S3File.ShareReceipt

  override fun toString() =
    "$bucketName/$userID/$datasetID/${S3File.SharesDir}/$recipientID/${file.baseName}"
}
