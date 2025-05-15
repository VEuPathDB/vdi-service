package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetShare
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.setDatasetID

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
