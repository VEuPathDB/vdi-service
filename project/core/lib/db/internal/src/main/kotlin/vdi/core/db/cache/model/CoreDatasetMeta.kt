package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetVisibility

interface CoreDatasetMeta {
  val datasetID: DatasetID
  val visibility: DatasetVisibility
  val name: String
  val projectName: String?
  val programName: String?
  val summary: String
  val description: String?
  val originalID: DatasetID?
}
