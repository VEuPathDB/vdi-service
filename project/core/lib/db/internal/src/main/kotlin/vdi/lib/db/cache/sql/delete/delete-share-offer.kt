package vdi.lib.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

internal fun Connection.deleteShareOffer(datasetID: DatasetID, recipientID: UserID) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
  } > 0
