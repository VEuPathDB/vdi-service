package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import vdi.component.db.cache.util.map
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.withPreparedStatement
import vdi.component.db.cache.util.withResults
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

internal fun Connection.selectUploadFiles(datasetID: DatasetID): List<VDIDatasetFileInfo> =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { map { VDIDatasetFileInfo(it.getString(1), it.getLong(2).toULong()) } }
  }
