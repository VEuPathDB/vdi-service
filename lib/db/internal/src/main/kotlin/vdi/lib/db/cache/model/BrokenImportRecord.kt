package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

data class BrokenImportRecord(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val typeName: DataType,
  val typeVersion: String,
  val projects: List<String>,
  val messages: List<String>,
)
