package vdi.core.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import java.sql.Types
import vdi.core.db.cache.model.AdminAllDatasetsRow
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.util.getDatasetVisibility
import vdi.core.db.cache.util.getFileDetailList
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.data.DatasetType

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
, m.short_name
, m.short_attribution
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
, array(
    SELECT (f.file_name, f.file_size)
    FROM vdi.upload_files AS f
    WHERE f.dataset_id = d.dataset_id
  ) AS upload_files
, array(
    SELECT (f.file_name, f.file_size)
    FROM vdi.install_files AS f
    WHERE f.dataset_id = d.dataset_id
  ) AS install_files
, r.original_id
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS m
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS i
    USING (dataset_id)
  LEFT JOIN vdi.import_messages AS s
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = d.dataset_id
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
          datasetID        = it.reqDatasetID("dataset_id"),
          ownerID          = it.getUserID("owner_id"),
          origin           = it.getString("origin"),
          created          = it.getDateTime("created"),
          isDeleted        = it.getBoolean("is_deleted"),
          type             = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
          name             = it.getString("name"),
          shortName        = it.getString("short_name"),
          shortAttribution = it.getString("short_attribution"),
          summary          = it.getString("summary"),
          description      = it.getString("description"),
          sourceURL        = it.getString("source_url"),
          visibility       = it.getDatasetVisibility("visibility"),
          projects         = it.getProjectIDList("projects"),
          importStatus     = it.getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          inserted         = it.getDateTime("inserted"),
          originalID       = it.optDatasetID("original_id"),
          importMessage    = it.getString("message"),
          uploadFiles      = it.getFileDetailList("upload_files"),
          installFiles     = it.getFileDetailList("install_files"),
        )
      }
    }
  }
}
