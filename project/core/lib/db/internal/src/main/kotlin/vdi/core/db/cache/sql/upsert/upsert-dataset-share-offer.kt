package vdi.core.db.cache.sql.upsert

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.data.DatasetShareOffer

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_share_offers (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT (dataset_id, recipient_id) DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertDatasetShareOffer(
  datasetID: DatasetID,
  recipientID: UserID,
  status: DatasetShareOffer.Action,
) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
    setString(3, status.toString())
    setString(4, status.toString())
  }
