package org.veupathdb.service.vdi.db.internal.queries

import org.veupathdb.service.vdi.generated.model.*
import org.veupathdb.service.vdi.model.DatasetListQuery
import org.veupathdb.service.vdi.model.DatasetListSortField
import org.veupathdb.service.vdi.model.DatasetOwnershipFilter
import org.veupathdb.service.vdi.model.SortOrder
import java.sql.Connection
import java.sql.Types


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
, md.name
, md.summary
, md.description
, array(SELECT project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
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
    vdi.recipient_share AS r
    INNER JOIN vdi.owner_share AS o
      USING (dataset_id, shared_with)
  WHERE
    r.status = 'accept'
    AND o.status = 'grant'
    AND r.shared_with = ?
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
fun Connection.selectDatasetList(query: DatasetListQuery) : List<DatasetListEntry> {
  // List of params that we will collect as we assemble the query.
  val params = ArrayList<Pair<Any, Int>>(12)

  // Figure out the ownership segment of the query
  val prefix = when (query.ownership) {
    // If the ownership filter was set to ANY then the query prefix will be a
    // union of 2 queries, the first finds dataset IDs for datasets owned by
    // the requesting user, the second finds dataset IDs for datasets shared
    // with the requesting user.
    DatasetOwnershipFilter.ANY    -> {
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

    ps.executeQuery().use { rs ->
      val out = ArrayList<DatasetListEntry>(12)

      while (rs.next()) {
        out.add(DatasetListEntryImpl().apply {
          datasetID = rs.getString(1)
          datasetType = DatasetTypeInfoImpl().apply {
            name = rs.getString(2)
            version = rs.getString(3)
          }
          owner = DatasetOwnerImpl().apply {
            userID = rs.getString(4).toLong()
          }
          name = rs.getString(5)
          summary = rs.getString(6)
          description = rs.getString(7)
          projectIDs = (rs.getArray(8).array as Array<String>).asList()
          status = DatasetStatusInfoImpl().apply {
            import = when (val v = rs.getString(9)) {
              "awaiting-import" -> DatasetImportStatus.AWAITINGIMPORT
              "importing"       -> DatasetImportStatus.IMPORTING
              "imported"        -> DatasetImportStatus.IMPORTED
              "failed"          -> DatasetImportStatus.IMPORTFAILED
              else              -> throw IllegalStateException("unrecognized status string received from database: $v")
            }
          }
        })
      }

      out
    }
  }
}