package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.withPreparedStatement
import java.sql.Connection

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.install_files (dataset_id, file_name, file_size)
VALUES
  (?, ?, ?)
"""

internal fun Connection.insertInstallFiles(datasetID: DatasetID, files: Map<String, Long>) {
  withPreparedStatement(SQL) {
    setString(1, datasetID.toString())

    for ((file, size) in files) {
      setString(2, file)
      setLong(3, size)
      addBatch()
    }

    executeBatch()
  }
}