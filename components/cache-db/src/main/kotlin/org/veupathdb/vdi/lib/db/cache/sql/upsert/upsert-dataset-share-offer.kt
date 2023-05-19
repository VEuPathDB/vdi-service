package org.veupathdb.vdi.lib.db.cache.sql.upsert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIShareOfferAction
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import org.veupathdb.vdi.lib.db.cache.util.setUserID
import java.sql.Connection

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
  status: VDIShareOfferAction,
) {
  prepareStatement(SQL).use { ps ->
    ps.setDatasetID(1, datasetID)
    ps.setUserID(2, recipientID)
    ps.setString(3, status.value)
    ps.setString(4, status.value)

    ps.execute()
  }
}
