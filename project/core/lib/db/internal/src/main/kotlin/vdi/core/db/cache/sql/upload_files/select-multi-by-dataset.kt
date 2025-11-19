package vdi.core.db.cache.sql.upload_files

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetFileInfo
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
SELECT
  file_name
, file_size
FROM
  vdi.upload_files
WHERE
  dataset_id = ?
"""

internal fun Connection.selectUploadFiles(datasetID: DatasetID): List<DatasetFileInfo> =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { map { DatasetFileInfo(it.getString(1), it.getLong(2).toULong()) } }
  }
