package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID

data class DatasetFileLinksImpl(
  override val datasetID: DatasetID,
  override val files: Collection<String>
) : DatasetFileLinks