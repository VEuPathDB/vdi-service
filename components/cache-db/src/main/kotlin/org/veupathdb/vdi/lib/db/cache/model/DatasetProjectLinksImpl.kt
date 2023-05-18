package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

data class DatasetProjectLinksImpl(
  override val datasetID: DatasetID,
  override val projects: Collection<String>
) : DatasetProjectLinks