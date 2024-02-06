package org.veupathdb.vdi.lib.s3.datasets.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

sealed interface VDDatasetShareFilePath : VDPath {
  val recipientID: UserID
  val fileName: String

  val isOffer: Boolean

  val isReceipt: Boolean
}

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