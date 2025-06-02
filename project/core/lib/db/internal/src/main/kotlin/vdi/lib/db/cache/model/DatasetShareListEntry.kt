package vdi.lib.db.cache.model

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.DatasetShareReceipt
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

data class DatasetShareListEntry(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val typeName: DataType,
  val typeVersion: String,
  val receiptStatus: DatasetShareReceipt.Action?,
  val projects: List<InstallTargetID>
)
