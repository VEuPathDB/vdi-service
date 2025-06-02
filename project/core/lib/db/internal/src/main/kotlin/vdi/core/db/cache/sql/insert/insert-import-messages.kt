package vdi.core.db.cache.sql.insert

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
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertImportMessages(datasetID: DatasetID, messages: String) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, messages)
  }
