package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetRecordImpl
import vdi.component.db.cache.util.*
import java.sql.Connection

// language=postgresql
private val SQL = """
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
, dm.source_url
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
          datasetID    = datasetID,
          typeName     = getString("type_name"),
          typeVersion  = getString("type_version"),
          ownerID      = getUserID("owner_id"),
          isDeleted    = getBoolean("is_deleted"),
          created      = getDateTime("created"),
          importStatus = getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          origin       = getString("origin"),
          visibility   = VDIDatasetVisibility.fromString(getString("visibility")),
          name         = getString("name"),
          summary      = getString("summary"),
          description  = getString("description"),
          sourceURL    = getString("source_url"),
          projects     = getProjectIDList("projects"),
          inserted     = getDateTime("inserted")
        )
    }
  }
