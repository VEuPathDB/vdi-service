package vdi.component.db.cache.sql

import java.sql.Connection
import vdi.components.common.fields.DatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.dataset_files (
    dataset_id
  , file_name
  )
VALUES
  (?, ?)
ON CONFLICT DO NOTHING
"""

internal fun Connection.insertDatasetFiles(datasetID: DatasetID, files: Iterable<String>) {
  prepareStatement(SQL).use { ps ->
    for (file in files) {
      ps.setString(1, datasetID.toString())
      ps.setString(2, file)
      ps.addBatch()
    }

    ps.executeBatch()
  }
}