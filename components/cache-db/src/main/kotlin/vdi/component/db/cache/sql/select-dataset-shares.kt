package vdi.component.db.cache.sql

import java.sql.Connection
import vdi.component.db.cache.model.DatasetShare
import vdi.component.db.cache.model.ShareOfferAction
import vdi.component.db.cache.model.ShareReceiptAction
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

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
    ps.setString(1, datasetID.toString())
    ps.executeQuery().use { rs ->
      while (rs.next()) {
        out.add(
          DatasetShare(
            recipientID   = UserID(rs.getString("recipient_id")),
            offerStatus   = ShareOfferAction.fromString(rs.getString("offer_status")),
            receiptStatus = rs.getString("receipt_status")?.let(ShareReceiptAction::fromString) ?: ShareReceiptAction.Accept
          )
        )
      }
    }
  }

  return out
}