package vdi.component.db.cache.model

import vdi.components.common.fields.DatasetID

interface DatasetFileLinks {
  val datasetID: DatasetID
  val files: Collection<String>
}