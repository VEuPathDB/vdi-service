package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.model.DatasetFile
import org.veupathdb.vdi.lib.db.cache.util.map
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import org.veupathdb.vdi.lib.db.cache.util.withPreparedStatement
import org.veupathdb.vdi.lib.db.cache.util.withResults
import java.sql.Connection

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

internal fun Connection.selectUploadFiles(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { map { DatasetFile(it.getString(1), it.getLong(2).toULong()) } }
  }