package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: List<ProjectID>
}
