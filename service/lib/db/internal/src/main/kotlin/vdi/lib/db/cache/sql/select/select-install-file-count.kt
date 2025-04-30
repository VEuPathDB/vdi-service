package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  count(1)
FROM
  vdi.install_files
WHERE
  dataset_id = ?
"""

internal fun Connection.selectInstallFileCount(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { next(); getInt(1) }
  }
