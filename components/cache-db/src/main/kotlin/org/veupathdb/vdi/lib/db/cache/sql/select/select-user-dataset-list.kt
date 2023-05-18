package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.model.*
import java.sql.Connection
import java.sql.Types
import java.time.OffsetDateTime

// TODO: filter out is_deleted?  or not?  maybe downstream of here?

// language=postgresql
private fun sqlBody(
  prefix: String,
  projectFilter: String,
  orderBy: String,
  order: String,
) = """
WITH dataset_ids AS (
$prefix
)
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, d.owner_id
, d.is_deleted
, d.created
, md.name
, md.summary
, md.description
, array(SELECT f.file_name FROM vdi.dataset_files AS f WHERE f.dataset_id = d.dataset_id) AS files
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
, ic.status
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS md
    USING (dataset_id)
  INNER JOIN vdi.import_control AS ic
    USING (dataset_id)
WHERE
  $projectFilter
ORDER BY
  $orderBy $order
OFFSET
  ?
LIMIT
  ?
"""

// language=postgresql
private const val PREFIX_OWNERSHIP_OWNED = """
  SELECT
    dataset_id
  FROM
    vdi.datasets
  WHERE
    owner_id = ?
"""

// language=postgresql
private const val PREFIX_OWNERSHIP_SHARED = """
  SELECT
    dataset_id
  FROM
    vdi.dataset_share_receipts AS r
    INNER JOIN vdi.dataset_share_offers AS o
      USING (dataset_id, recipient_id)
  WHERE
    r.status = 'accept'
    AND o.status = 'grant'
    AND r.recipient_id = ?
"""

// language=postgresql
private const val PREFIX_OWNERSHIP_ANY = """
$PREFIX_OWNERSHIP_OWNED
  UNION
$PREFIX_OWNERSHIP_SHARED
"""

private const val PROJECT_ID_FILTER = "EXISTS (SELECT 1 FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id AND p.project_id = ?)"

private const val DUMMY_FILTER = "1 = 1"

private const val ORDER_BY_CREATED = "created"
private const val ORDER_BY_NAME    = "name"

private const val ORDER_ASC = "ASC"
private const val ORDER_DESC = "DESC"


@Suppress("UNCHECKED_CAST")
fun Connection.selectDatasetList(query: DatasetListQuery) : List<DatasetRecord> {
  // List of params that we will collect as we assemble the query.
  val params = ArrayList<Pair<Any, Int>>(12)

  // Figure out the ownership segment of the query
  val prefix = when (query.ownership) {
    // If the ownership filter was set to ANY then the query prefix will be a
    // union of 2 queries, the first finds dataset IDs for datasets owned by
    // the requesting user, the second finds dataset IDs for datasets shared
    // with the requesting user.
    DatasetOwnershipFilter.ANY -> {
      val ownerID = query.userID.toString()
      params.add(ownerID to Types.VARCHAR)
      params.add(ownerID to Types.VARCHAR)
      PREFIX_OWNERSHIP_ANY
    }

    // If the ownership filter was set to OWNED, then the query prefix will be a
    // query that finds dataset IDs for datasets owned by the requesting user.
    DatasetOwnershipFilter.OWNED  -> {
      params.add(query.userID.toString() to Types.VARCHAR)
      PREFIX_OWNERSHIP_OWNED
    }

    // If the ownership filter was set to SHARED, then the query prefix will be
    // a query that finds dataset IDs for datasets shared with the requesting
    // user.
    DatasetOwnershipFilter.SHARED -> {
      params.add(query.userID.toString() to Types.VARCHAR)
      PREFIX_OWNERSHIP_SHARED
    }
  }

  // Figure out the project filter.
  val projectFilter = when (query.projectID) {
    // If the given project ID is null then don't apply a filter (use the dummy
    // filter of `1 = 1`).
    null -> DUMMY_FILTER

    // If the given project ID is not null, then apply a filter that ensures the
    // returned datasets have a record linking them to the target project ID.
    else -> {
      params.add(query.projectID to Types.VARCHAR)
      PROJECT_ID_FILTER
    }
  }

  val orderBy = when (query.sortField) {
    DatasetListSortField.CREATION_TIMESTAMP -> ORDER_BY_CREATED
    DatasetListSortField.NAME               -> ORDER_BY_NAME
  }

  val sortOrder = when(query.sortOrder) {
    SortOrder.ASCENDING  -> ORDER_ASC
    SortOrder.DESCENDING -> ORDER_DESC
  }

  return prepareStatement(sqlBody(prefix, projectFilter, orderBy, sortOrder)).use { ps ->
    var i = 0
    for ((param, type) in params)
      ps.setObject(++i, param, type)

    ps.setInt(++i, query.offset)
    ps.setInt(++i, query.limit)

    ps.executeQuery().use { rs ->
      val out = ArrayList<DatasetRecord>(12)

      while (rs.next()) {
        out.add(DatasetRecordImpl(
          datasetID    = DatasetID(rs.getString(1)),
          typeName     = rs.getString(2),
          typeVersion  = rs.getString(3),
          ownerID      = UserID(rs.getString(4)),
          isDeleted    = rs.getBoolean(5),
          created      = rs.getObject(6, OffsetDateTime::class.java),
          importStatus = DatasetImportStatus.fromString(rs.getString(12)),
          name         = rs.getString(7),
          summary      = rs.getString(8),
          description  = rs.getString(9),
          files        = rs.getArray(10).toList(),
          projects     = rs.getArray(11).toList(),
        ))
      }

      out
    }
  }
}

@Suppress("UNCHECKED_CAST")
private fun java.sql.Array.toList() = (this.array as Array<String>).asList()