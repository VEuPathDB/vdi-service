package vdi.core.db.cache.sql.dataset_share_offers

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.data.UserID

// language=postgresql
private const val MULTI_SQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetShareOffers(datasetID: DatasetID) =
  withPreparedUpdate(MULTI_SQL) { setDatasetID(1, datasetID) }

// language=postgresql
private const val SINGLE_SQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

internal fun Connection.deleteShareOffer(datasetID: DatasetID, recipientID: UserID) =
  withPreparedUpdate(SINGLE_SQL) {
    setDatasetID(1, datasetID)
    setUserID(2, recipientID)
  } > 0
