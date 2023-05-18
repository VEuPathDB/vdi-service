package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

data class DatasetShareListEntry(
  val datasetID: DatasetID,
  val ownerID: UserID,
)