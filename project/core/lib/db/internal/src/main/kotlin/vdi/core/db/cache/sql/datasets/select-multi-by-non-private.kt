package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.model.DatasetRecord
import vdi.core.db.cache.model.DatasetRecordImpl
import vdi.core.db.cache.util.getDatasetVisibility
import vdi.core.db.cache.util.getImportStatus
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.*
import vdi.model.meta.DatasetType

// language=postgresql
private const val SQL = """
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

internal fun Connection.selectNonPrivateDatasets(): List<DatasetRecord> {
  return withPreparedStatement(SQL) {
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
