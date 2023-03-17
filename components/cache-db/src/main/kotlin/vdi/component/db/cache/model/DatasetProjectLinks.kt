package vdi.component.db.cache.model

import vdi.components.common.fields.DatasetID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: Collection<String>
}