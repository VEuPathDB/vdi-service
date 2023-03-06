package org.veupathdb.service.vdi.db.internal.queries

import org.veupathdb.service.vdi.generated.model.ShareReceiptAction
import java.sql.Connection
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

private const val STATUS_ENUM_ACCEPT_VALUE = "accept"
private const val STATUS_ENUM_REJECT_VALUE = "reject"

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_share_receipts (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertDatasetShareReceipt(
  datasetID: DatasetID,
  recipientID: UserID,
  status: ShareReceiptAction,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, recipientID.toString())
    ps.setString(3, status.toDBStatus())
    ps.setString(4, status.toDBStatus())
  }
}

private fun ShareReceiptAction.toDBStatus() =
  when (this) {
    ShareReceiptAction.ACCEPT -> STATUS_ENUM_ACCEPT_VALUE
    ShareReceiptAction.REJECT -> STATUS_ENUM_REJECT_VALUE
  }