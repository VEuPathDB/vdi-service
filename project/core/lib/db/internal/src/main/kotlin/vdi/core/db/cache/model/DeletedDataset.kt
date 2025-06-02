package vdi.core.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

data class DeletedDataset(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val projects: List<InstallTargetID>,
  val dataType: DatasetType,
)
