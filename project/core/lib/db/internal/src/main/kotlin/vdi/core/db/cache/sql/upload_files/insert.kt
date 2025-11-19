package vdi.core.db.cache.sql.upload_files

import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetFileInfo
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.upload_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
ON CONFLICT
  DO NOTHING
"""

internal fun Connection.tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<DatasetFileInfo>) =
  withPreparedBatchUpdate(SQL, files) {
    setDatasetID(1, datasetID)
    setString(2, it.name)
    setLong(3, it.size.toLong())
  }.reduceOrNull(Int::plus) ?: 0
