package org.veupathdb.vdi.lib.db.cache.sql.upsert

import org.veupathdb.vdi.lib.common.field.DatasetID
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

internal fun Connection.upsertImportMessages(datasetID: DatasetID, messages: String) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setString(2, messages)
      ps.setString(3, messages)
      ps.executeUpdate()
    }
}