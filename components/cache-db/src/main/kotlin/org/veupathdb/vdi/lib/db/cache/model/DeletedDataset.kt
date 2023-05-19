package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

data class DeletedDataset(
  val datasetID: DatasetID,
  val ownerID: UserID,
)