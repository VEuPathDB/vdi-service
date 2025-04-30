package vdi.lib.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.upload_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
ON CONFLICT
  DO NOTHING
"""

internal fun Connection.tryInsertUploadFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>) =
  withPreparedBatchUpdate(SQL, files) {
    setDatasetID(1, datasetID)
    setString(2, it.filename)
    setLong(3, it.size.toLong())
  }.reduce(Int::plus)
