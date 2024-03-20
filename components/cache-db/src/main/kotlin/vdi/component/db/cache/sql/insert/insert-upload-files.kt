package vdi.component.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import vdi.component.db.cache.util.withPreparedStatement
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.upload_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
ON CONFLICT DO NOTHING
"""

internal fun Connection.insertUploadFiles(datasetID: DatasetID, files: Iterable<VDIDatasetFileInfo>) {
  withPreparedStatement(SQL) {
    setString(1, datasetID.toString())

    files.forEach {
      setString(2, it.filename)
      setLong(3, it.fileSize)
      addBatch()
    }

    executeBatch()
  }
}