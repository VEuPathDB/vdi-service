package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import java.sql.Types
import vdi.core.db.cache.model.*
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.util.getDatasetVisibility
import vdi.core.db.cache.util.getImportStatus
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.data.DatasetType

// language=postgresql
private fun sqlBody(
  prefix: String,
  projectFilter: String,
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
, d.origin
, d.created
, d.inserted
, md.name
, md.short_name
, md.short_attribution
, md.summary
, md.description
, md.visibility
, md.source_url
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
, ic.status
, r.original_id
FROM
  dataset_ids AS did
  INNER JOIN vdi.datasets AS d
    USING (dataset_id)
  INNER JOIN vdi.dataset_metadata AS md
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS ic
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = did.dataset_id
WHERE
  d.is_deleted = FALSE
  $projectFilter
ORDER BY
  d.created DESC
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

private const val PROJECT_ID_FILTER = "AND EXISTS (SELECT 1 FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id AND p.project_id = ?)"

private const val DUMMY_FILTER = "AND 1 = 1"

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

  return prepareStatement(sqlBody(prefix, projectFilter)).use { ps ->
    var i = 0
    for ((param, type) in params)
      ps.setObject(++i, param, type)

    ps.withResults {
      map {
        DatasetRecordImpl(
          datasetID        = it.reqDatasetID("dataset_id"),
          type             = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
          ownerID          = it.getUserID("owner_id"),
          isDeleted        = it.getBoolean("is_deleted"),
          created          = it.getDateTime("created"),
          importStatus     = it.getImportStatus("status") ?: DatasetImportStatus.Queued,
          visibility       = it.getDatasetVisibility("visibility"),
          origin           = it.getString("origin"),
          name             = it.getString("name"),
          shortName        = it.getString("short_name"),
          shortAttribution = it.getString("short_attribution"),
          summary          = it.getString("summary"),
          description      = it.getString("description"),
          sourceURL        = it.getString("source_url"),
          projects         = it.getProjectIDList("projects"),
          inserted         = it.getDateTime("inserted"),
          originalID       = it.optDatasetID("original_id")
        )
      }
    }
  }
}
