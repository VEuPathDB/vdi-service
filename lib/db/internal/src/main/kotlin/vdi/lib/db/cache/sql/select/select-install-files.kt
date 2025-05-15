package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
SELECT
  file_name
, file_size
FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

internal fun Connection.selectInstallFiles(datasetID: DatasetID): List<VDIDatasetFileInfo> =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { map { VDIDatasetFileInfo(it.getString(1), it.getLong(2).toULong()) } }
  }
