package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.db.cache.model.DatasetRecordImpl
import vdi.lib.db.cache.util.getDatasetVisibility
import vdi.lib.db.cache.util.getImportStatus
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.jdbc.*

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
, dm.short_name
, dm.short_attribution
, dm.summary
, dm.description
, dm.visibility
, dm.source_url
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

internal fun Connection.selectDataset(datasetID: DatasetID): DatasetRecord? =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults {
      if (!next())
        null
      else
        DatasetRecordImpl(
          datasetID        = datasetID,
          typeName         = getDataType("type_name"),
          typeVersion      = getString("type_version"),
          ownerID          = getUserID("owner_id"),
          isDeleted        = getBoolean("is_deleted"),
          origin           = getString("origin"),
          created          = getDateTime("created"),
          inserted         = getDateTime("inserted"),
          name             = getString("name"),
          shortName        = getString("short_name"),
          shortAttribution = getString("short_attribution"),
          summary          = getString("summary"),
          description      = getString("description"),
          visibility       = getDatasetVisibility("visibility"),
          sourceURL        = getString("source_url"),
          projects         = getProjectIDList("projects"),
          importStatus     = getImportStatus("status") ?: DatasetImportStatus.Queued,
          originalID       = optDatasetID("original_id"),
        )
    }
  }
