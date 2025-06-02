package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.lib.db.cache.model.AdminDatasetDetailsRecord
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.util.getDatasetVisibility
import vdi.lib.db.cache.util.getImportStatus
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.cache.util.getStringList
import vdi.lib.db.jdbc.*
import vdi.lib.db.model.SyncControlRecord

// language=postgresql
private const val SQL_BASE = """
SELECT
  d.dataset_id
, d.owner_id
, d.origin
, d.created
, d.type_name
, d.type_version
, d.inserted
, m.name
, m.short_name
, m.short_attribution
, m.summary
, m.description
, m.source_url
, m.visibility
, array(
    SELECT p.project_id
    FROM vdi.dataset_projects AS p
    WHERE p.dataset_id = d.dataset_id
  ) AS projects
, array(SELECT m.message FROM vdi.import_messages AS m WHERE m.dataset_id = d.dataset_id) AS messages
, array(SELECT file_name FROM vdi.install_files AS if WHERE if.dataset_id = d.dataset_id) AS install_files
, array(SELECT file_name FROM vdi.upload_files AS if WHERE if.dataset_id = d.dataset_id) AS upload_files
, i.status
, sc.shares_update_time
, sc.data_update_time
, sc.meta_update_time
, r.original_id
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS m
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS i
    USING (dataset_id)
  LEFT JOIN vdi.sync_control AS sc
    USING (dataset_id)
  LEFT JOIN vdi.dataset_revisions AS r
    ON r.revision_id = d.dataset_id
WHERE
  d.is_deleted = FALSE
AND
  d.dataset_id = ?
"""

internal fun Connection.selectAdminDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord? {
  val sql = SQL_BASE

  return withPreparedStatement(sql) {
    setString(1, datasetID.toString())
    withResults {
      if (!this.next()) {
        return null
      }

      AdminDatasetDetailsRecord(
        datasetID        = reqDatasetID("dataset_id"),
        ownerID          = getUserID("owner_id"),
        origin           = getString("origin"),
        created          = getDateTime("created"),
        inserted         = getDateTime("inserted"),
        typeName         = getDataType("type_name"),
        typeVersion      = getString("type_version"),
        name             = getString("name"),
        shortName        = getString("short_name"),
        shortAttribution = getString("short_attribution"),
        summary          = getString("summary"),
        description      = getString("description"),
        sourceURL        = getString("source_url"),
        visibility       = getDatasetVisibility("visibility"),
        projects         = getProjectIDList("projects"),
        importStatus     = getImportStatus("status") ?: DatasetImportStatus.Queued,
        originalID       = optDatasetID("original_id"),
        syncControl      = SyncControlRecord(
          datasetID     = reqDatasetID("dataset_id"),
          sharesUpdated = getDateTime("shares_update_time"),
          dataUpdated   = getDateTime("data_update_time"),
          metaUpdated   = getDateTime("meta_update_time")
        ),
        messages         = getStringList("messages"),
        installFiles     = getStringList("install_files"),
        uploadFiles      = getStringList("upload_files"),
        isDeleted        = false,
      )
    }
  }
}
