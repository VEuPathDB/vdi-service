package vdi.lib.db.cache.query

import vdi.lib.db.cache.model.DatasetOwnershipFilter
import vdi.model.data.UserID

data class DatasetListQuery(
  val userID:    UserID,
  val projectID: String?,
  val ownership: DatasetOwnershipFilter,
)
