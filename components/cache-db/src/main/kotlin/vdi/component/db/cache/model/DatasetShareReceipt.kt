package vdi.component.db.cache.model

import org.veupathdb.service.vdi.generated.model.ShareReceiptAction
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

interface DatasetShareReceipt {
  val datasetID: DatasetID
  val recipientID: UserID
  val action: ShareReceiptAction
}