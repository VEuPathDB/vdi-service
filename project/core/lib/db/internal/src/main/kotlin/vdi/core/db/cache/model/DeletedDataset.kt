package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
import vdi.model.meta.UserID

data class DeletedDataset(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val projects: List<InstallTargetID>,
  val dataType: DatasetType,
)
