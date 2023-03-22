package vdi.component.db.cache.model

import vdi.components.common.fields.UserID

data class DatasetShare(
  val recipientID: UserID,
  val offerStatus: ShareOfferAction,
  val receiptStatus: ShareReceiptAction,
)