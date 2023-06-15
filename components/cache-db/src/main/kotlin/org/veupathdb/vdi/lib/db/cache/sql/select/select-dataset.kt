package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecordImpl
import org.veupathdb.vdi.lib.db.cache.util.*
import java.sql.Connection

// language=postgresql
private val SQL = """
SELECT
  vd.type_name
, vd.type_version
, vd.owner_id
, vd.is_deleted
, vd.created
, dm.name
, dm.summary
, dm.description
, array(SELECT f.file_name FROM vdi.dataset_files AS f WHERE f.dataset_id = vd.dataset_id) AS files
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, ic.status
FROM
  vdi.datasets vd
  INNER JOIN vdi.dataset_metadata dm
    USING (dataset_id)
  LEFT JOIN vdi.import_control ic
    USING (dataset_id)
WHERE
  vd.dataset_id = ?
"""

internal fun Connection.selectDataset(datasetID: DatasetID): DatasetRecord? =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults {
      if (!next())
        null
      else
        DatasetRecordImpl(
          datasetID,
          getString("type_name"),
          getString("type_version"),
          getUserID("owner_id"),
          getBoolean("is_deleted"),
          getDateTime("created"),
          getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          getString("name"),
          getString("summary"),
          getString("description"),
          getStringList("files"),
          getProjectIDList("projects"),
        )
    }
  }
