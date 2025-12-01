package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.*
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID

internal object ImportMessagesTable {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }

  // language=postgresql
  private const val SelectSQL = """
SELECT
  message
FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

  context(con: Connection)
  internal fun select(datasetID: DatasetID) =
    con.withPreparedStatement(SelectSQL) {
      setDatasetID(1, datasetID)
      withResults { map { it.getString(1)!! } }
    }

  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.import_messages (
    dataset_id
  , message
  )
VALUES
  (?, ?)
ON CONFLICT DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(datasetID: DatasetID, messages: Iterable<String>) =
    con.withPreparedBatchUpdate(InsertSQL, messages) {
      setDatasetID(1, datasetID)
      setString(2, it)
    }.reduceOrNull(Int::plus) ?: 0

}