package vdi.core.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.UserID

data class BrokenImportRecord(
  val datasetID: DatasetID,
  val ownerID:   UserID,
  val type:      DatasetType,
  val projects:  List<String>,
  val messages:  List<String>,
)
