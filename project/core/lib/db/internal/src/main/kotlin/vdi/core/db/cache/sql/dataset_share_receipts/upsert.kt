package vdi.core.db.cache.sql.dataset_share_receipts

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.meta.DatasetShareReceipt

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_share_receipts (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT (dataset_id, recipient_id) DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertDatasetShareReceipt(
  datasetID: DatasetID,
  recipientID: UserID,
  status: DatasetShareReceipt.Action,
) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
    setString(3, status.toString())
    setString(4, status.toString())
  }
