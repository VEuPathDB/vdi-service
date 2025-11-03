package vdi.core.db.cache.query

/**
 * Represents inputs for a prepared query that fetches data from the cache
 * database for the "all datasets" admin endpoint.
 */
data class AdminAllDatasetsQuery(

  /**
   * Result offset, used for pagination.
   */
  val offset: UInt,

  /**
   * Result count limit, 0 = no limit.
   */
  val limit: UInt,

  /**
   * Project ID filter, null = no filter.
   */
  val projectID: String?,

  /**
   * Whether datasets that have been marked as deleted should be included in the
   * query results.
   */
  val includeDeleted: Boolean,
)
