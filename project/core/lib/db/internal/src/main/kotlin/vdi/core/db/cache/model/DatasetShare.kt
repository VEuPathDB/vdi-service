package vdi.core.db.cache.model

import vdi.model.meta.UserID
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.DatasetShareReceipt

data class DatasetShare(
  val recipientID: UserID,
  val offerStatus: DatasetShareOffer.Action?,
  val receiptStatus: DatasetShareReceipt.Action?,
)
