package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import org.veupathdb.vdi.lib.db.cache.model.DatasetShare
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dso.recipient_id
, dso.status AS offer_status
, dsr.status AS receipt_status
FROM
  vdi.dataset_share_offers AS dso
  LEFT JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dataset_id = ?
"""

internal fun Connection.selectSharesFor(datasetID: DatasetID): List<DatasetShare> {
  val out = ArrayList<DatasetShare>()

  prepareStatement(SQL).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShare(
            recipientID   = UserID(rs.getString("recipient_id")),
            offerStatus   = VDIShareOfferAction.fromString(rs.getString("offer_status")),
            receiptStatus = rs.getString("receipt_status")?.let(VDIShareReceiptAction::fromString) ?: VDIShareReceiptAction.Accept
          )
        )
      }
    }
  }

  return out
}