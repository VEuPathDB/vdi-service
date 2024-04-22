package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import vdi.component.db.cache.model.DatasetShare
import vdi.component.db.cache.util.*
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  dso.recipient_id
, dso.status AS offer_status
, dsr.status AS receipt_status
FROM
  vdi.dataset_share_offers AS dso
  FULL OUTER JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
WHERE
  dataset_id = ?
"""

internal fun Connection.selectSharesFor(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults {
      map {
        DatasetShare(
          recipientID   = it.getUserID("recipient_id"),
          offerStatus   = it.getString("offer_status")?.let(VDIShareOfferAction::fromString),
          receiptStatus = it.getString("receipt_status")?.let(VDIShareReceiptAction::fromString)
        )
      }
    }
  }
