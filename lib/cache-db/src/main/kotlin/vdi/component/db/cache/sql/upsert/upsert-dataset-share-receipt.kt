package vdi.component.db.cache.sql.upsert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareReceiptAction
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.setUserID
import java.sql.Connection

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
  status: VDIShareReceiptAction,
) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
    setString(3, status.value)
    setString(4, status.value)
  }
