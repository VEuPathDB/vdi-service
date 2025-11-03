package vdi.core.db.cache.sql.upload_files

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
SELECT
  count(1)
FROM
  vdi.upload_files
WHERE
  dataset_id = ?
"""

internal fun Connection.selectUploadFileCount(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { next(); getInt(1) }
  }
