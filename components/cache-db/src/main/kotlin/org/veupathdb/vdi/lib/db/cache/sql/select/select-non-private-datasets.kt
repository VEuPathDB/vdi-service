package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecordImpl
import org.veupathdb.vdi.lib.db.cache.util.*
import java.sql.Connection

// language=postgresql
private const val SQL = """
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
, md.visibility
, array(SELECT f.file_name FROM vdi.dataset_files AS f WHERE f.dataset_id = d.dataset_id) AS files
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = d.dataset_id) AS projects
, ic.status
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS md
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS ic
    USING (dataset_id)
WHERE
  md.visibility != 'private'
"""

internal fun Connection.selectNonPrivateDatasets(): List<DatasetRecord> {
  return withPreparedStatement(SQL) {
    withResults {
      map {
        DatasetRecordImpl(
          datasetID    = getDatasetID("dataset_id"),
          typeName     = getString("type_name"),
          typeVersion  = getString("type_version"),
          ownerID      = getUserID("owner_id"),
          isDeleted    = getBoolean("is_deleted"),
          created      = getDateTime("created"),
          importStatus = getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          visibility   = VDIDatasetVisibility.fromString(getString("visibility")),
          name         = getString("name"),
          summary      = getString("summary"),
          description  = getString("description"),
          files        = getStringList("files"),
          projects     = getProjectIDList("projects")
        )
      }
    }
  }
}