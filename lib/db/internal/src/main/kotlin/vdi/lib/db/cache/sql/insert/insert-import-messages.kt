package vdi.lib.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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
