package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
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

internal fun Connection.tryInsertImportMessages(datasetID: DatasetID, messages: String) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setDatasetID(1, datasetID)
      ps.setString(2, messages)
      ps.executeUpdate()
    }
}