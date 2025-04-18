package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility

data class DatasetMetaImpl(
  override val datasetID: DatasetID,
  override val visibility: VDIDatasetVisibility,
  override val name: String,
  override val shortName: String?,
  override val shortAttribution: String?,
  override val category: String?,
  override val summary: String?,
  override val description: String?,
  override val sourceURL: String?,
) : DatasetMeta
