package vdi.component.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.setUserID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_receipts
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

internal fun Connection.deleteShareReceipt(datasetID: DatasetID, recipientID: UserID) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
  }
