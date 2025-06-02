package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import vdi.model.data.DatasetFileInfo
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID

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
