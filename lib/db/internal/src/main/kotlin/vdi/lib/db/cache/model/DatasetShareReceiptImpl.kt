package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction

data class DatasetShareReceiptImpl(
  override val datasetID: DatasetID,
  override val recipientID: UserID,
  override val action: VDIShareReceiptAction
) : DatasetShareReceipt
