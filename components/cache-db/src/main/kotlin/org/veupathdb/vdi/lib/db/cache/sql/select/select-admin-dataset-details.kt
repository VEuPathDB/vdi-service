package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.cache.model.AdminDatasetDetailsRecord
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.util.*
import java.sql.Connection
import java.time.OffsetDateTime

// TODO: This query selects import_messages as a left join as well as an array agg.  Remove one of them.

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
, s.message
, sc.shares_update_time
, sc.data_update_time
, sc.meta_update_time
FROM
  vdi.datasets AS d
  INNER JOIN vdi.dataset_metadata AS m
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS i
    USING (dataset_id)
  LEFT JOIN vdi.import_messages AS s
    USING (dataset_id)
  LEFT JOIN vdi.sync_control AS sc
    USING (dataset_id)
WHERE
  d.is_deleted = FALSE
AND
  d.dataset_id = ?
"""


/**
 * Selects the total count of datasets that match the given filter for the
 * "all datasets" admin endpoint.
 */
internal fun Connection.selectAdminDatasetDetails(datasetID: DatasetID): AdminDatasetDetailsRecord? {
  val sql = SQL_BASE

  return withPreparedStatement(sql) {
    setString(1, datasetID.toString())
    withResults {
        if (!this.next()) {
          return null
        }
        AdminDatasetDetailsRecord(
          datasetID     = getDatasetID("dataset_id"),
          ownerID       = getUserID("owner_id"),
          origin        = getString("origin"),
          created       = getObject("created", OffsetDateTime::class.java),
          inserted      = getDateTime("inserted"),
          typeName      = getString("type_name"),
          typeVersion   = getString("type_version"),
          name          = getString("name"),
          summary       = getString("summary"),
          description   = getString("description"),
          sourceURL     = getString("source_url"),
          visibility    = VDIDatasetVisibility.fromString(getString("visibility")),
          projectIDs    = getProjectIDList("projects"),
          importStatus  = getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          importMessage = getString("message"),
          syncControl = VDISyncControlRecord(
            datasetID = getDatasetID("dataset_id"),
            sharesUpdated = getDateTime("shares_update_time"),
            dataUpdated = getDateTime("data_update_time"),
            metaUpdated = getDateTime("meta_update_time")
          ),
          messages = getStringList("messages"),
          installFiles = getStringList("install_files"),
          uploadFiles = getStringList("upload_files"),
          isDeleted = false,
        )
    }
  }
}