package vdi.core.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_receipts
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

internal fun Connection.deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
  } > 0
