package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: Collection<String>
}