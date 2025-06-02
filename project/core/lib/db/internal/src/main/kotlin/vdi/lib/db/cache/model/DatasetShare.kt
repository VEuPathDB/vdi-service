package vdi.lib.db.cache.model

import vdi.model.data.UserID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt

data class DatasetShare(
  val recipientID: UserID,
  val offerStatus: DatasetShareOffer.Action?,
  val receiptStatus: DatasetShareReceipt.Action?,
)
