package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import java.sql.Types
import vdi.lib.db.cache.query.AdminAllDatasetsQuery

// language=postgresql
private const val SQL_BASE = """
SELECT
  count(1)
FROM
  vdi.datasets AS d
WHERE
  1 = 1
"""

private const val DELETED_FILTER = """
  AND d.is_deleted = FALSE
"""

private const val PROJECT_ID_FILTER = """
  AND exists(
    SELECT
      1
    FROM
      vdi.dataset_projects AS p
    WHERE
      p.dataset_id = d.dataset_id
      AND p.project_id = ?
  )
"""

/**
 * Selects the total count of datasets that match the given filter for the
 * "all datasets" admin endpoint.
 */
internal fun Connection.selectAdminAllDatasetCount(query: AdminAllDatasetsQuery): UInt {
  val params = ArrayList<Pair<Int, Any>>(1)

  var sql = SQL_BASE

  if (!query.includeDeleted) {
    sql += DELETED_FILTER
  }

  if (query.projectID != null) {
    sql += PROJECT_ID_FILTER
    params.add(Types.VARCHAR to query.projectID)
  }

  return withPreparedStatement(sql) {
    // Apply the params to the prepared statement.
    for (i in params.indices)
      setObject(i+1, params[i].second, params[i].first)

    withResults {
      next()
      getLong(1).toUInt()
    }
  }
}
