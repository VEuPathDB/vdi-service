package vdi.core.db.cache.model

import vdi.model.meta.DatasetID

data class DatasetProjectLinksImpl(
  override val datasetID: DatasetID,
  override val projects: List<String>
) : DatasetProjectLinks
