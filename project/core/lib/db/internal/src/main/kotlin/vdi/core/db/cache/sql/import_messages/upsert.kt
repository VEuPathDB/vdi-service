package vdi.core.db.cache.sql.import_messages

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.meta.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.import_messages (
    dataset_id
  , message
  )
VALUES
  (?, ?)
ON CONFLICT DO NOTHING
"""

internal fun Connection.upsertImportMessages(datasetID: DatasetID, messages: Iterable<String>) =
  withPreparedBatchUpdate(SQL, messages) {
    setDatasetID(1, datasetID)
    setString(2, it)
  }.reduceOrNull(Int::plus) ?: 0
