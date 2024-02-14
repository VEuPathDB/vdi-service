package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.cache.model.AdminAllDatasetsRow
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.query.AdminAllDatasetsQuery
import org.veupathdb.vdi.lib.db.cache.util.*
import java.sql.Connection
import java.sql.Types
import java.time.OffsetDateTime

// language=postgresql
private const val SQL_BASE = """
SELECT
  d.dataset_id
, d.owner_id
, d.origin
, d.created
, d.type_name
, d.type_version
, d.is_deleted
, d.inserted
, m.name
, m.summary
, m.description
, m.source_url
, m.visibility
, array(
    SELECT p.project_id
    FROM vdi.dataset_projects AS p
    WHERE p.dataset_id = d.dataset_id
  ) AS projects
, i.status
, s.message
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS m
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS i
    USING (dataset_id)
  LEFT JOIN vdi.import_messages AS s
    USING (dataset_id)
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

private const val ORDER_BY = """
  ORDER BY d.created ASC
"""

private fun queryLimit(limit: UInt) = """
  LIMIT $limit
"""

private fun queryOffset(offset: UInt) = """
  OFFSET $offset
"""

/**
 * Selects the total count of datasets that match the given filter for the
 * "all datasets" admin endpoint.
 */
internal fun Connection.selectAdminAllDatasets(query: AdminAllDatasetsQuery): List<AdminAllDatasetsRow> {
  val params = ArrayList<Pair<Int, Any>>(1)

  var sql = SQL_BASE

  if (!query.includeDeleted)
    sql += DELETED_FILTER

  if (query.projectID != null) {
    sql += PROJECT_ID_FILTER
    params.add(Types.VARCHAR to query.projectID)
  }

  sql += ORDER_BY

  if (query.limit > 0u)
    sql += queryLimit(query.limit)

  if (query.offset > 0u)
    sql += queryOffset(query.offset)

  return withPreparedStatement(sql) {
    // Apply the params to the prepared statement.
    for (i in params.indices)
      setObject(i+1, params[i].second, params[i].first)

    withResults {
      map {
        AdminAllDatasetsRow(
          datasetID     = it.getDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          origin        = it.getString("origin"),
          created       = it.getObject("created", OffsetDateTime::class.java),
          isDeleted     = it.getBoolean("is_deleted"),
          typeName      = it.getString("type_name"),
          typeVersion   = it.getString("type_version"),
          name          = it.getString("name"),
          summary       = it.getString("summary"),
          description   = it.getString("description"),
          sourceURL     = it.getString("source_url"),
          visibility    = VDIDatasetVisibility.fromString(it.getString("visibility")),
          projectIDs    = it.getProjectIDList("projects"),
          importStatus  = it.getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          importMessage = it.getString("message"),
          inserted      = it.getDateTime("inserted"),
        )
      }
    }
  }
}