package vdi.core.db.cache.sql.complex

import io.foxcapades.kdbc.*
import java.sql.Connection
import java.sql.Types
import vdi.core.db.cache.model.*
import vdi.core.db.cache.query.DatasetListQuery
import vdi.core.db.cache.util.getDatasetVisibility
import vdi.core.db.cache.util.getImportStatus
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.meta.*

internal object DatasetQueries {

  // language=postgresql
  private const val SelectByIDSQL = """
SELECT
  vd.type_name
, vd.type_version
, vd.owner_id
, vd.is_deleted
, vd.origin
, vd.created
, vd.inserted
, dm.name
, dm.summary
, dm.description
, dm.visibility
, dm.program_name
, dm.project_name
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, ic.status
, r.original_id
FROM
  vdi.datasets vd
  INNER JOIN vdi.dataset_metadata dm
    USING (dataset_id)
  LEFT JOIN vdi.import_control ic
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = vd.dataset_id
WHERE
  vd.dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID): DatasetRecord? =
    con.withPreparedStatement(SelectByIDSQL) {
      setDatasetID(1, datasetID)
      withResults {
        if (!next())
          null
        else
          DatasetRecordImpl(
            datasetID    = datasetID,
            type         = DatasetType(getDataType("type_name"), getString("type_version")),
            ownerID      = getUserID("owner_id"),
            isDeleted    = getBoolean("is_deleted"),
            origin       = getString("origin"),
            created      = getDateTime("created"),
            inserted     = getDateTime("inserted"),
            name         = getString("name"),
            projectName  = getString("project_name"),
            programName  = getString("program_name"),
            summary      = getString("summary"),
            description  = getString("description"),
            visibility   = getDatasetVisibility("visibility"),
            projects     = getProjectIDList("projects"),
            importStatus = getImportStatus("status") ?: DatasetImportStatus.Queued,
            originalID   = optDatasetID("original_id"),
          )
      }
    }

  // language=postgresql
  private val SelectForUserSQL = """
SELECT
  vd.type_name
, vd.type_version
, vd.owner_id
, vd.is_deleted
, vd.origin
, vd.created
, vd.inserted
, dm.name
, dm.visibility
, dm.summary
, dm.project_name
, dm.program_name
, dm.description
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, ic.status
, r.original_id
FROM
  vdi.datasets vd
  INNER JOIN vdi.dataset_metadata AS dm
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS ic
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = vd.dataset_id
WHERE
  vd.dataset_id = ?
  AND (
    vd.owner_id = ?
    OR EXISTS (
      SELECT
        1
      FROM
        vdi.dataset_share_receipts dsr
        INNER JOIN vdi.dataset_share_offers dso
          USING(dataset_id, recipient_id)
        WHERE
          recipient_id = ?
          AND dataset_id = vd.dataset_id
          AND dsr.status = '${DatasetShareReceipt.Action.Accept}'
          AND dso.status = '${DatasetShareOffer.Action.Grant}'
    )
  )
"""

  context(con: Connection)
  internal fun select(userID: UserID, datasetID: DatasetID): DatasetRecord? =
    con.withPreparedStatement(SelectForUserSQL) {
      setDatasetID(1, datasetID)
      setUserID(2, userID)
      setUserID(3, userID)

      withResults {
        if (!next())
          null
        else
          DatasetRecordImpl(
            datasetID    = datasetID,
            type         = DatasetType(getDataType("type_name"), getString("type_version")),
            ownerID      = getUserID("owner_id"),
            isDeleted    = getBoolean("is_deleted"),
            created      = getDateTime("created"),
            importStatus = getImportStatus("status") ?: DatasetImportStatus.Queued,
            origin       = getString("origin"),
            visibility   = getDatasetVisibility("visibility"),
            name         = getString("name"),
            projectName  = getString("project_name"),
            programName  = getString("program_name"),
            summary      = getString("summary"),
            description  = getString("description"),
            projects     = getProjectIDList("projects"),
            inserted     = getDateTime("inserted"),
            originalID   = optDatasetID("original_id")
          )
      }
    }

  // language=postgresql
  private const val SelectByPublicationSQL = """
WITH root_publications AS (
  SELECT
    publication_id
  FROM
    vdi.dataset_publications
  WHERE
    dataset_id = ?
)
SELECT
  p.dataset_id       -- 1
, d.type_name
, d.type_version     -- 3
, m.name
, m.summary          -- 5
, d.created
, p.publication_id   -- 7
, p.publication_type
FROM
  vdi.dataset_publications p
  INNER JOIN vdi.datasets d
    USING (dataset_id)
  INNER JOIN vdi.dataset_metadata m
    USING (dataset_id)
WHERE
  dataset_id != ?
  AND publication_id IN (SELECT publication_id FROM root_publications)
"""

  context(con: Connection)
  internal fun selectByCommonPublication(rootDatasetID: DatasetID) =
    con.withPreparedStatement(SelectByPublicationSQL) {
      setDatasetID(1, rootDatasetID)
      setDatasetID(2, rootDatasetID)

      withResults { map {
        RelatedDataset(
          datasetID    = DatasetID(it[1]),
          datasetType  = DatasetType(DataType.of(it[2]), it[3]),
          name         = it[4],
          summary      = it[5],
          created      = it[6],
          relationType = RelatedDataset.RelationType.Publication,
          publication  = RelatedDataset.PublicationRef(it[7], DatasetPublication.PublicationType.entries[it[8]])
        )
      } }
    }

  // language=postgresql
  private const val SelectByProgramNameSQL = """
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, m.name
, m.summary
, d.created
FROM
  vdi.datasets d
  INNER JOIN vdi.dataset_metadata m
    USING (dataset_id)
WHERE
  d.owner_id = ?
  AND m.program_name = ?
"""

  context(con: Connection)
  internal fun selectByProgramName(ownerID: UserID, programName: String) =
    con.withPreparedStatement(SelectByProgramNameSQL) {
      setUserID(1, ownerID)
      setString(2, programName)

      withResults { map {
        RelatedDataset(
          datasetID    = DatasetID(it[1]),
          datasetType  = DatasetType(DataType.of(it[2]), it[3]),
          name         = it[4],
          summary      = it[5],
          created      = it[6],
          relationType = RelatedDataset.RelationType.ProgramName,
        )
      } }
    }

  // language=postgresql
  private const val SelectByProjectNameSQL = """
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, m.name
, m.summary
, d.created
FROM
  vdi.datasets d
  INNER JOIN vdi.dataset_metadata m
    USING (dataset_id)
WHERE
  d.owner_id = ?
  AND m.project_name = ?
"""

  context(con: Connection)
  internal fun selectByProjectName(ownerID: UserID, projectName: String) =
    con.withPreparedStatement(SelectByProjectNameSQL) {
      setUserID(1, ownerID)
      setString(2, projectName)

      withResults { map {
        RelatedDataset(
          datasetID    = DatasetID(it[1]),
          datasetType  = DatasetType(DataType.of(it[2]), it[3]),
          name         = it[4],
          summary      = it[5],
          created      = it[6],
          relationType = RelatedDataset.RelationType.ProjectName,
        )
      } }
    }


  // language=postgresql
  private fun customQuerySQLBody(
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
, md.project_name
, md.program_name
, md.summary
, md.description
, md.visibility
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

  context(con: Connection)
  fun select(query: DatasetListQuery) : List<DatasetRecord> {
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

    return con.prepareStatement(customQuerySQLBody(prefix, projectFilter)).use { ps ->
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
            projectName      = it.getString("project_name"),
            programName      = it.getString("program_name"),
            summary          = it.getString("summary"),
            description      = it.getString("description"),
            projects         = it.getProjectIDList("projects"),
            inserted         = it.getDateTime("inserted"),
            originalID       = it.optDatasetID("original_id")
          )
        }
      }
    }
  }

  // language=postgresql
  private const val SelectDeletedSQL = """
SELECT
  dataset_id
, owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, type_name
, type_version
FROM
  vdi.datasets AS vd
WHERE
  is_deleted = TRUE
"""

  context(con: Connection)
  internal fun selectDeleted(): List<DeletedDataset> =
    con.createStatement().withStatementResults(SelectDeletedSQL) { map {
      DeletedDataset(
        it.reqDatasetID("dataset_id"),
        it.getUserID("owner_id"),
        it.getProjectIDList("projects"),
        DatasetType(it.getDataType("type_name"), it.getString("type_version"))
      )
    } }


  // language=postgresql
  private const val SelectNonPrivateSQL = """
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
, md.project_name
, md.program_name
, md.summary
, md.description
, md.visibility
, array(
  SELECT p.project_id
  FROM vdi.dataset_projects AS p
  WHERE p.dataset_id = d.dataset_id
) AS projects
, ic.status
, r.original_id
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS md
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS ic
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = d.dataset_id
WHERE
  md.visibility != 'private'
"""

  context(con: Connection)
  internal fun selectNonPrivateDatasets(): List<DatasetRecord> =
    con.withPreparedStatement(SelectNonPrivateSQL) {
      withResults {
        map {
          DatasetRecordImpl(
            datasetID    = reqDatasetID("dataset_id"),
            type         = DatasetType(it.getDataType("type_name"), it.getString("type_version")),
            ownerID      = getUserID("owner_id"),
            isDeleted    = getBoolean("is_deleted"),
            created      = getDateTime("created"),
            importStatus = getImportStatus("status") ?: DatasetImportStatus.Queued,
            visibility   = getDatasetVisibility("visibility"),
            origin       = getString("origin"),
            name         = getString("name"),
            projectName  = getString("project_name"),
            programName  = getString("program_name"),
            summary      = getString("summary"),
            description  = getString("description"),
            projects     = getProjectIDList("projects"),
            inserted     = getDateTime("inserted"),
            originalID   = optDatasetID("original_id"),
          )
        }
      }
    }

}