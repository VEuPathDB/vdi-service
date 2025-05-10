package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetShare
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID

// language=postgresql
private const val SQL = """
SELECT
  ids.dataset_id
, dso.recipient_id
, dso.status AS offer_status
, dsr.status AS receipt_status
FROM
  unnest(?) AS ids(dataset_id)
  INNER JOIN vdi.dataset_share_offers AS dso
    USING (dataset_id)
  LEFT JOIN vdi.dataset_share_receipts AS dsr
    USING (dataset_id, recipient_id)
"""

internal fun Connection.selectSharesFor(datasetIDs: List<DatasetID>): Map<DatasetID, List<DatasetShare>> {
  val out = HashMap<DatasetID, MutableList<DatasetShare>>(datasetIDs.size)

  withPreparedStatement(SQL) {
    setArray(1, createArrayOf("varchar", datasetIDs.toTypedArray()))
    withResults {
      forEach {
        val dsID = reqDatasetID("dataset_id")
        out.computeIfAbsent(dsID) { ArrayList() }.add(DatasetShare(
          recipientID   = getUserID("recipient_id"),
          offerStatus   = VDIShareOfferAction.fromString(getString("offer_status")),
          receiptStatus = getString("receipt_status")?.let(VDIShareReceiptAction::fromString) ?: VDIShareReceiptAction.Accept
        ))
      }
    }
  }

  return out
}
