package vdi.core.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.DatasetShareReceipt
import vdi.model.data.UserID

class ShareReceiptRecord(
  val datasetID: DatasetID,
  val recipientID: UserID,
  action: Action
): DatasetShareReceipt(action)

