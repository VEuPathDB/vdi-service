package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID

data class DeletedDataset(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val projects: List<ProjectID>,
  val dataType: DataType,
)
