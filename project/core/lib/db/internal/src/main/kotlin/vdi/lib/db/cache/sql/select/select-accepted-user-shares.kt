package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetShareListEntry
import vdi.lib.db.cache.util.getProjectIDList
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.jdbc.setUserID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt
import vdi.model.data.UserID

// language=postgresql
private val SQL = """
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
  INNER JOIN vdi.datasets AS ds
    USING (dataset_id)
  INNER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dso.recipient_id = ?
  AND ds.is_deleted = FALSE
  AND dso.status = '${DatasetShareOffer.Action.Grant}'
  AND dsr.status = '${DatasetShareReceipt.Action.Accept}'
"""

internal fun Connection.selectAcceptedSharesFor(userID: UserID) =
  withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.reqDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          typeName      = it.getDataType("type_name"),
          typeVersion   = it.getString("type_version"),
          receiptStatus = DatasetShareReceipt.Action.Accept,
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
