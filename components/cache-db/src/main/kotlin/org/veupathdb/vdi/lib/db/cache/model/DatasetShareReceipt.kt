package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction

interface DatasetShareReceipt {
  val datasetID: DatasetID
  val recipientID: UserID
  val action: VDIShareReceiptAction
}

