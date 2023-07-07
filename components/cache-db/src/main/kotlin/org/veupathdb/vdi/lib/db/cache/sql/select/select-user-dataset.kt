package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecord
import org.veupathdb.vdi.lib.db.cache.model.DatasetRecordImpl
import org.veupathdb.vdi.lib.db.cache.util.*
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import org.veupathdb.vdi.lib.db.cache.util.setUserID
import org.veupathdb.vdi.lib.db.cache.util.withPreparedStatement
import org.veupathdb.vdi.lib.db.cache.util.withResults
import java.sql.Connection
import java.time.OffsetDateTime

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
, dm.visibility
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
          AND dsr.status = '${VDIShareReceiptAction.Accept.value}'
          AND dso.status = '${VDIShareOfferAction.Grant.value}'
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
          datasetID,
          getString("type_name"),
          getString("type_version"),
          getUserID("owner_id"),
          getBoolean("is_deleted"),
          getDateTime("created"),
          getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued,
          VDIDatasetVisibility.fromString(getString("visibility")),
          getString("name"),
          getString("summary"),
          getString("description"),
          getStringList("files"),
          getProjectIDList("projects"),
        )
    }
  }
