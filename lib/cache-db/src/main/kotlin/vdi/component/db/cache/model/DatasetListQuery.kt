package vdi.component.db.cache.model

data class DatasetListQuery(
  val userID: Long,
  val projectID: String?,
  val ownership: DatasetOwnershipFilter,
)