package vdi.lib.db.cache.model

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.UserID

data class BrokenImportRecord(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val typeName: DataType,
  val typeVersion: String,
  val projects: List<String>,
  val messages: List<String>,
)
