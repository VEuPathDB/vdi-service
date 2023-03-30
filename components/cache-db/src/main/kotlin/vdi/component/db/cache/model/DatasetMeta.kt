package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

interface DatasetMeta {
  val datasetID: DatasetID
  val name: String
  val summary: String?
  val description: String?
}