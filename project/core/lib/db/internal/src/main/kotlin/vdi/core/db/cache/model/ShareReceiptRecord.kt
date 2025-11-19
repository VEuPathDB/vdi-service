package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetShareReceipt
import vdi.model.meta.UserID

class ShareReceiptRecord(
  val datasetID: DatasetID,
  val recipientID: UserID,
  action: Action
): DatasetShareReceipt(action)

