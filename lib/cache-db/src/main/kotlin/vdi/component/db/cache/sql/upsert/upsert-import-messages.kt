package vdi.component.db.cache.sql.upsert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.jdbc.setDatasetID
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
