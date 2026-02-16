package vdi.core.db.cache.sql.complex

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import java.sql.Types
import vdi.core.db.cache.model.*
import vdi.core.db.cache.query.AdminAllDatasetsQuery
import vdi.core.db.cache.util.*
import vdi.core.db.jdbc.*
import vdi.core.db.model.SyncControlRecord
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType

internal object DatasetAdminQueries {

  // region Select Target Dataset

  // language=postgresql
  private const val SelectByIDSQL = """
SELECT
  d.dataset_id
, d.owner_id
, d.origin
, d.created
, d.type_name
, d.type_version
, d.inserted
, m.name
, m.summary
, m.description
, m.project_name
, m.program_name
, m.visibility
, array(
    SELECT p.project_id
    FROM vdi.dataset_projects AS p
    WHERE p.dataset_id = d.dataset_id
  ) AS projects
, array(SELECT m.message FROM vdi.import_messages AS m WHERE m.dataset_id = d.dataset_id) AS messages
, array(SELECT file_name FROM vdi.install_files AS if WHERE if.dataset_id = d.dataset_id) AS install_files
, array(SELECT file_name FROM vdi.upload_files AS if WHERE if.dataset_id = d.dataset_id) AS upload_files
, i.status AS import_status
, sc.shares_update_time
, sc.data_update_time
, sc.meta_update_time
, r.original_id
, us.status AS upload_status
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS m
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS i
    USING (dataset_id)
  LEFT JOIN vdi.sync_control AS sc
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = d.dataset_id
  LEFT JOIN vdi.upload_status AS us
    USING (dataset_id)
WHERE
  d.is_deleted = FALSE
AND
  d.dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID): AdminDatasetDetailsRecord? =
    con.withPreparedStatement(SelectByIDSQL) {
      setString(1, datasetID.toString())
      withResults {
        if (!this.next()) {
          return null
        }

        val importStatus = getImportStatus("import_status")

        AdminDatasetDetailsRecord(
          datasetID    = reqDatasetID("dataset_id"),
          ownerID      = getUserID("owner_id"),
          origin       = getString("origin"),
          created      = getDateTime("created"),
          inserted     = getDateTime("inserted"),
          type         = DatasetType(getDataType("type_name"), getString("type_version")),
          name         = getString("name"),
          projectName  = getString("project_name"),
          programName  = getString("program_name"),
          summary      = getString("summary"),
          description  = getString("description"),
          visibility   = getDatasetVisibility("visibility"),
          projects     = getProjectIDList("projects"),
          importStatus = importStatus,
          originalID   = optDatasetID("original_id"),
          syncControl  = SyncControlRecord(
            datasetID  = reqDatasetID("dataset_id"),
            sharesUpdated = getDateTime("shares_update_time"),
            dataUpdated   = getDateTime("data_update_time"),
            metaUpdated   = getDateTime("meta_update_time")
          ),
          messages     = getStringList("messages"),
          installFiles = getStringList("install_files"),
          uploadFiles  = getStringList("upload_files"),
          isDeleted    = false,
          uploadStatus = getString("upload_status")?.let(DatasetUploadStatus.Companion::fromString)
            ?: when (importStatus) {
              null -> DatasetUploadStatus.Running
              else -> DatasetUploadStatus.Success
            }
        )
      }
    }

  // endregion Select Target Dataset

  // region Select by Query

  // language=postgresql
  private const val SelectAllSQLStart = """
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
, m.project_name
, m.program_name
, m.summary
, m.description
, m.visibility
, array(
    SELECT p.project_id
    FROM vdi.dataset_projects AS p
    WHERE p.dataset_id = d.dataset_id
  ) AS projects
, i.status as import_status
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
, us.status AS upload_status
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
  LEFT JOIN vdi.upload_status AS us
    USING (dataset_id)
WHERE
  1 = 1
"""

  private const val SelectAllSQLFilterNonDeleted = """
  AND d.is_deleted = FALSE
"""

  private const val SelectAllSQLFilterInstallTarget = """
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

  private const val SelectAllSQLOrderBy = """
  ORDER BY d.created ASC
"""

  private fun queryLimit(limit: UInt) = """
  LIMIT $limit
"""

  private fun queryOffset(offset: UInt) = """
  OFFSET $offset
"""

  context(con: Connection)
  internal fun select(query: AdminAllDatasetsQuery): List<AdminAllDatasetsRow> {
    val params = ArrayList<Pair<Int, Any>>(1)

    var sql = SelectAllSQLStart

    if (!query.includeDeleted)
      sql += SelectAllSQLFilterNonDeleted

    if (query.projectID != null) {
      sql += SelectAllSQLFilterInstallTarget
      params.add(Types.VARCHAR to query.projectID)
    }

    sql += SelectAllSQLOrderBy

    if (query.limit > 0u)
      sql += queryLimit(query.limit)

    if (query.offset > 0u)
      sql += queryOffset(query.offset)

    return con.withPreparedStatement(sql) {
      // Apply the params to the prepared statement.
      for (i in params.indices)
        setObject(i+1, params[i].second, params[i].first)

      withResults {
        map {

          val importStatus = getImportStatus("import_status")

          AdminAllDatasetsRow(
            datasetID     = it.reqDatasetID("dataset_id"),
            ownerID       = it.getUserID("owner_id"),
            origin        = it.getString("origin"),
            created       = it.getDateTime("created"),
            isDeleted     = it.getBoolean("is_deleted"),
            type          = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
            name          = it.getString("name"),
            projectName   = it.getString("project_name"),
            programName   = it.getString("program_name"),
            summary       = it.getString("summary"),
            description   = it.getString("description"),
            visibility    = it.getDatasetVisibility("visibility"),
            projects      = it.getProjectIDList("projects"),
            importStatus  = importStatus,
            inserted      = it.getDateTime("inserted"),
            originalID    = it.optDatasetID("original_id"),
            importMessage = it.getString("message"),
            uploadFiles   = it.getFileDetailList("upload_files"),
            installFiles  = it.getFileDetailList("install_files"),
            uploadStatus = getString("upload_status")?.let(DatasetUploadStatus.Companion::fromString)
              ?: when (importStatus) {
                null -> DatasetUploadStatus.Running
                else -> DatasetUploadStatus.Success
              }
          )
        }
      }
    }
  }

  // endregion Select by Query

  // region Select Count by Query

  // language=postgresql
  private const val SelectCountSQL = """
SELECT
  count(1)
FROM
  vdi.datasets AS d
WHERE
  1 = 1
"""

  /**
   * Selects the total count of datasets that match the given filter for the
   * "all datasets" admin endpoint.
   */
  context(con: Connection)
  internal fun count(query: AdminAllDatasetsQuery): UInt {
    val params = ArrayList<Pair<Int, Any>>(1)

    var sql = SelectCountSQL

    if (!query.includeDeleted) {
      sql += SelectAllSQLFilterNonDeleted
    }

    if (query.projectID != null) {
      sql += SelectAllSQLFilterInstallTarget
      params.add(Types.VARCHAR to query.projectID)
    }

    return con.withPreparedStatement(sql) {
      // Apply the params to the prepared statement.
      for (i in params.indices)
        setObject(i+1, params[i].second, params[i].first)

      withResults {
        next()
        getLong(1).toUInt()
      }
    }
  }

  // endregion Select Count by Query

  // region Broken Imports

  private fun makeBrokenImportSQL(
    userIDFilter: String,
    beforeFilter: String,
    afterFilter:  String,
    orderBy:      String,
    sortOrder:    String,
    limit:        UInt,
    offset:       UInt,
  ) =
// language=postgresql
    """
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, d.owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
, array(SELECT m.message FROM vdi.import_messages AS m WHERE m.dataset_id = d.dataset_id) AS messages
FROM
  vdi.datasets AS d
  INNER JOIN vdi.import_control AS i
    USING (dataset_id)
WHERE
  i.status = 'failed'
  $userIDFilter
  $beforeFilter
  $afterFilter
ORDER BY
  $orderBy $sortOrder
LIMIT
  $limit
OFFSET
  $offset
"""

  context(con: Connection)
  internal fun selectBrokenImports(query: BrokenImportListQuery): List<BrokenImportRecord> {
    val params = ArrayList<Pair<Any, Int>>(3)

    val userIDFilter: String
    val beforeFilter: String
    val afterFilter: String

    if (query.hasUserID) {
      userIDFilter = "AND d.owner_id = ?"
      params.add(query.userID!!.toString() to Types.VARCHAR)
    } else {
      userIDFilter = ""
    }

    if (query.hasBefore) {
      beforeFilter = "AND d.created < ?"
      params.add(query.before!! to Types.TIMESTAMP_WITH_TIMEZONE)
    } else {
      beforeFilter = ""
    }

    if (query.hasAfter) {
      afterFilter = "AND d.created > ?"
      params.add(query.after!! to Types.TIMESTAMP_WITH_TIMEZONE)
    } else {
      afterFilter = ""
    }

    val orderBy = when (query.sortBy) {
      BrokenImportListQuery.SortField.Date -> "d.created"
    }

    val sql = makeBrokenImportSQL(
      userIDFilter = userIDFilter,
      beforeFilter = beforeFilter,
      afterFilter  = afterFilter,
      orderBy      = orderBy,
      sortOrder    = query.order.toString(),
      limit        = query.limit,
      offset       = query.offset
    )

    return con.withPreparedStatement(sql) {
      params.forEachIndexed { i, pair ->
        setObject(i+1, pair.first, pair.second)
      }

      withResults {
        val out = ArrayList<BrokenImportRecord>(query.limit.toInt())

        forEach {
          out.add(BrokenImportRecord(
            datasetID   = reqDatasetID("dataset_id"),
            ownerID     = getUserID("owner_id"),
            type        = DatasetType(getDataType("type_name"), getString("type_version")),
            projects    = getProjectIDList("projects"),
            messages    = getStringList("messages"),
          ))
        }

        out
      }
    }
  }

  // endregion Broken Imports

}