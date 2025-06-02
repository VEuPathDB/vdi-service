package vdi.lib.db.cache.model

import vdi.model.data.DatasetID

data class DatasetProjectLinksImpl(
  override val datasetID: DatasetID,
  override val projects: List<String>
) : DatasetProjectLinks
