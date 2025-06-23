package vdi.core.db.cache.sql.dataset_share_offers

import io.foxcapades.kdbc.forEach
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.DatasetShare
import vdi.core.db.jdbc.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetShareOffer
import vdi.model.data.DatasetShareReceipt

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
          offerStatus   = DatasetShareOffer.Action.fromString(getString("offer_status")),
          receiptStatus = getString("receipt_status")?.let(DatasetShareReceipt.Action::fromString) ?: DatasetShareReceipt.Action.Accept
        ))
      }
    }
  }

  return out
}
