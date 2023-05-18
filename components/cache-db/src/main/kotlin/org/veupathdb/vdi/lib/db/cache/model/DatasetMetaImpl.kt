package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

data class DatasetMetaImpl(
  override val datasetID: DatasetID,
  override val name: String,
  override val summary: String?,
  override val description: String?
) : DatasetMeta