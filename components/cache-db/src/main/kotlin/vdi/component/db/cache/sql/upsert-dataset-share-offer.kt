package vdi.component.db.cache.sql

import java.sql.Connection
import vdi.component.db.cache.model.ShareOfferAction
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_share_offers (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertDatasetShareOffer(
  datasetID: DatasetID,
  recipientID: UserID,
  status: ShareOfferAction,
) {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.setString(2, recipientID.toString())
    ps.setString(3, status.value)
    ps.setString(4, status.value)

    ps.execute()
  }
}
