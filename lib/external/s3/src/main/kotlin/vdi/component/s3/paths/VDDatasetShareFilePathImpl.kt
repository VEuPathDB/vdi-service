package vdi.component.s3.paths

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
    get() = fileName == S3Paths.ShareOfferFileName

  override val isReceipt
    get() = fileName == S3Paths.ShareReceiptFileName

  override fun toString() =
    "$bucketName/$userID/$datasetID/${S3Paths.SharesDirName}/$recipientID/$fileName"
}
