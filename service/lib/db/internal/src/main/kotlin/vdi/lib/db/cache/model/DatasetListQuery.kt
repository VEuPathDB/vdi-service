package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.UserID

data class DatasetListQuery(
  val userID:    UserID,
  val projectID: String?,
  val ownership: DatasetOwnershipFilter,
)
