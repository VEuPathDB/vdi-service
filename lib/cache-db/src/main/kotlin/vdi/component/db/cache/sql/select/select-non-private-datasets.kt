package vdi.component.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetRecord
import vdi.component.db.cache.model.DatasetRecordImpl
import vdi.component.db.cache.util.getDatasetVisibility
import vdi.component.db.cache.util.getImportStatus
import vdi.component.db.cache.util.getProjectIDList
import vdi.component.db.jdbc.getDataType
import vdi.component.db.jdbc.getDatasetID
import vdi.component.db.jdbc.getDateTime
import vdi.component.db.jdbc.getUserID
import java.sql.Connection

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
, md.short_name
, md.short_attribution
, md.category
, md.summary
, md.description
, md.visibility
, md.source_url
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
          datasetID        = getDatasetID("dataset_id"),
          typeName         = getDataType("type_name"),
          typeVersion      = getString("type_version"),
          ownerID          = getUserID("owner_id"),
          isDeleted        = getBoolean("is_deleted"),
          created          = getDateTime("created"),
          importStatus     = getImportStatus("status") ?: DatasetImportStatus.Queued,
          visibility       = getDatasetVisibility("visibility"),
          origin           = getString("origin"),
          name             = getString("name"),
          shortName        = getString("short_name"),
          shortAttribution = getString("short_attribution"),
          category         = getString("category"),
          summary          = getString("summary"),
          description      = getString("description"),
          sourceURL        = getString("source_url"),
          projects         = getProjectIDList("projects"),
          inserted         = getDateTime("inserted")
        )
      }
    }
  }
}
