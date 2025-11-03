package vdi.core.s3.files

import vdi.model.data.UserID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt

sealed interface DatasetShare {
  /**
   * ID of the share recipient.
   */
  val recipientID: UserID

  /**
   * A handle on this share offer's offer file.
   */
  val offer: DatasetShareFile<DatasetShareOffer>

  /**
   * A handle on this share offer's receipt file.
   */
  val receipt: DatasetShareFile<DatasetShareReceipt>

  companion object {
    fun create(
      recipientID: UserID,
      offer: DatasetShareFile<DatasetShareOffer>,
      receipt: DatasetShareFile<DatasetShareReceipt>,
    ): DatasetShare = Impl(recipientID, offer, receipt)
  }

  private data class Impl(
    override val recipientID: UserID,
    override val offer: DatasetShareFile<DatasetShareOffer>,
    override val receipt: DatasetShareFile<DatasetShareReceipt>,
  ): DatasetShare
}

