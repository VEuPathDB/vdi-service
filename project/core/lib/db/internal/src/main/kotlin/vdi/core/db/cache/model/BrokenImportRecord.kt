package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.UserID

data class BrokenImportRecord(
  val datasetID: DatasetID,
  val ownerID:   UserID,
  val type:      DatasetType,
  val installTargets:  List<String>,
  val messages:  List<String>,
) {
  fun datasetIdAsString() = datasetID.asString
}
