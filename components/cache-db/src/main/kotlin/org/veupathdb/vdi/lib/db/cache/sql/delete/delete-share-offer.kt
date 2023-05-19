package org.veupathdb.vdi.lib.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import org.veupathdb.vdi.lib.db.cache.util.setUserID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

internal fun Connection.deleteShareOffer(datasetID: DatasetID, recipientID: UserID) =
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
  }
