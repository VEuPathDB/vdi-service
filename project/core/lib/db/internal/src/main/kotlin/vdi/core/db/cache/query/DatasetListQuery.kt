package vdi.core.db.cache.query

import vdi.core.db.cache.model.DatasetOwnershipFilter
import vdi.model.data.UserID

data class DatasetListQuery(
  val userID:    UserID,
  val projectID: String?,
  val ownership: DatasetOwnershipFilter,
)
