package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setUserID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.UserID

internal object DatasetShareOffersTable {

  // language=postgresql
  private const val DeleteAllSQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteAllSQL) { setDatasetID(1, datasetID) }

  // language=postgresql
  private const val DeleteSingleSQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
  AND recipient_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID, recipientID: UserID) =
    con.withPreparedUpdate(DeleteSingleSQL) {
      setDatasetID(1, datasetID)
      setUserID(2, recipientID)
    } > 0

  // language=postgresql
  private const val UpsertSQL = """
INSERT INTO
  vdi.dataset_share_offers (dataset_id, recipient_id, status)
VALUES
  (?, ?, ?)
ON CONFLICT (dataset_id, recipient_id) DO UPDATE
SET
  status = ?
"""

  context(con: Connection)
  internal fun upsert(
    datasetID: DatasetID,
    recipientID: UserID,
    status: DatasetShareOffer.Action,
  ) =
    con.withPreparedUpdate(UpsertSQL) {
      setDatasetID(1, datasetID)
      setUserID(2, recipientID)
      setString(3, status.toString())
      setString(4, status.toString())
    }

}