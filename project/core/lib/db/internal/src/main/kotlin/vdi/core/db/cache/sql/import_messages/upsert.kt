package vdi.core.db.cache.sql.import_messages

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
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
ON CONFLICT (dataset_id) DO UPDATE
SET
  message = ?
"""

internal fun Connection.upsertImportMessages(datasetID: DatasetID, messages: String) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, messages)
    setString(3, messages)
  }
