package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

interface DatasetFileLinks {
  val datasetID: DatasetID
  val files: Collection<String>
}