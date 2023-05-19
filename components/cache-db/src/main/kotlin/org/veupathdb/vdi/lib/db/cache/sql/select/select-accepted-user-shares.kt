package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.consts.OfferStatus
import org.veupathdb.vdi.lib.db.cache.consts.ReceiptStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry
import org.veupathdb.vdi.lib.db.cache.util.*
import java.sql.Connection

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
  INNER JOIN vdi.datasets AS ds
    USING (dataset_id)
  INNER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dso.recipient_id = ?
  AND ds.is_deleted = FALSE
  AND dso.status = '${OfferStatus.Granted}'
  AND dsr.status = '${ReceiptStatus.Accepted}'
"""

internal fun Connection.selectAcceptedSharesFor(userID: UserID) =
  withPreparedStatement(SQL) {
    setUserID(1, userID)
    withResults {
      map {
        DatasetShareListEntry(
          datasetID     = it.getDatasetID("dataset_id"),
          ownerID       = it.getUserID("owner_id"),
          typeName      = it.getString("type_name"),
          typeVersion   = it.getString("type_version"),
          receiptStatus = VDIShareReceiptAction.Accept,
          projects      = it.getProjectIDList("projects")
        )
      }
    }
  }
