package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

interface DatasetMeta {
  val datasetID: DatasetID
  val visibility: VDIDatasetVisibility
  val name: String
  val summary: String?
  val description: String?
  val sourceURL: String?
}