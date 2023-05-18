package org.veupathdb.vdi.lib.db.cache.model

data class DatasetListQuery(
  val userID: Long,
  val projectID: String?,
  val ownership: DatasetOwnershipFilter,
  val offset: Int,
  val limit: Int,
  val sortField: DatasetListSortField,
  val sortOrder: SortOrder,
)