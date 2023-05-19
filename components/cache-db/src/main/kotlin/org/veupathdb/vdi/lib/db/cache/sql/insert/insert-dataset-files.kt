package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_files (
    dataset_id
  , file_name
  )
VALUES
  (?, ?)
ON CONFLICT (dataset_id, file_name) DO NOTHING
"""

internal fun Connection.tryInsertDatasetFiles(datasetID: DatasetID, files: Iterable<String>) {
  prepareStatement(SQL).use { ps ->
    for (file in files) {
      ps.setDatasetID(1, datasetID)
      ps.setString(2, file)
      ps.addBatch()
    }

    ps.executeBatch()
  }
}