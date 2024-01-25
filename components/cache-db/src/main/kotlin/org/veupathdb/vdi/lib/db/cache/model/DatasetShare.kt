package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction

data class DatasetShare(
  val recipientID: UserID,
  val offerStatus: VDIShareOfferAction?,
  val receiptStatus: VDIShareReceiptAction?,
)