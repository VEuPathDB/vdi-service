package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection
import vdi.lib.db.cache.consts.OfferStatus
import vdi.lib.db.cache.model.DatasetShareListEntry
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.jdbc.setUserID

// language=postgresql
private const val SQL = """
SELECT
  dso.dataset_id
, ds.owner_id
, ds.type_name
, ds.type_version
, ARRAY(
    SELECT
      project_id
    FROM
      vdi.dataset_projects dp
    WHERE
      dso.dataset_id = dp.dataset_id
  ) AS projects
FROM
  vdi.dataset_share_offers AS dso
  INNER JOIN vdi.datasets  AS ds
    USING (dataset_id)
WHERE
  dso.recipient_id = ?
  AND dso.status = '${OfferStatus.Granted}'
  AND ds.is_deleted = FALSE
  AND NOT EXISTS(
    SELECT
      1
    FROM
      vdi.dataset_share_receipts AS dsr
    WHERE
      dso.dataset_id = dsr.dataset_id
      AND dso.recipient_id = dsr.recipient_id
  )
"""

internal fun Connection.selectOpenSharesFor(userID: UserID) =
  withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.reqDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          typeName      = it.getDataType("type_name"),
          typeVersion   = it.getString("type_version"),
          receiptStatus = null,
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
