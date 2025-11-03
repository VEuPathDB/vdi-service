package vdi.core.db.cache.model

import vdi.model.data.*

data class DatasetShareListEntry(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val type: DatasetType,
  val receiptStatus: DatasetShareReceipt.Action?,
  val projects: List<InstallTargetID>
)
