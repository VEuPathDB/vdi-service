package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.consts.OfferStatus
import org.veupathdb.vdi.lib.db.cache.consts.ReceiptStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetShareListEntry
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dso.dataset_id
, ds.owner_id
FROM
  vdi.dataset_share_offers AS dso
  INNER JOIN vdi.datasets AS ds
    USING (dataset_id)
  INNER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  recipient_id = ?
  AND dso.status = '${OfferStatus.Granted}'
  AND dsr.status = '${ReceiptStatus.Rejected}'
"""

internal fun Connection.selectRejectedSharesFor(userID: UserID): List<DatasetShareListEntry> {
  val out = ArrayList<DatasetShareListEntry>()

  prepareStatement(SQL).use { ps ->
    ps.setString(1, userID.toString())
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShareListEntry(
            datasetID = DatasetID(rs.getString("dataset_id")),
            ownerID   = UserID(rs.getString("owner_id")),
          )
        )
      }
    }
  }

  return out
}
