package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.model.DatasetRecord
import vdi.lib.db.cache.model.DatasetRecordImpl
import vdi.lib.db.cache.util.getDatasetVisibility
import vdi.lib.db.cache.util.getImportStatus
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.jdbc.*
import vdi.model.data.*

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

internal fun Connection.selectDatasetForUser(userID: UserID, datasetID: DatasetID): DatasetRecord? =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, userID)
    setUserID(3, userID)

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
          created          = getDateTime("created"),
          importStatus     = getImportStatus("status") ?: DatasetImportStatus.Queued,
          origin           = getString("origin"),
          visibility       = getDatasetVisibility("visibility"),
          name             = getString("name"),
          shortName        = getString("short_name"),
          shortAttribution = getString("short_attribution"),
          summary          = getString("summary"),
          description      = getString("description"),
          sourceURL        = getString("source_url"),
          projects         = getProjectIDList("projects"),
          inserted         = getDateTime("inserted"),
          originalID       = optDatasetID("original_id")
        )
    }
  }
