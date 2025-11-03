package vdi.core.db.cache.sql.install_files

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.data.DatasetID
import vdi.model.data.DatasetFileInfo
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.install_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
ON CONFLICT
  DO NOTHING
"""

internal fun Connection.tryInsertInstallFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
  withPreparedBatchUpdate(SQL, files) {
    setDatasetID(1, datasetID)
    setString(2, it.name)
    setLong(3, it.size.toLong())
  }
    .reduceOrNull { a, b -> a + b } ?: 0
