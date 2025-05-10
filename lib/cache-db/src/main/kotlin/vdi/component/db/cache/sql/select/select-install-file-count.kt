package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.withPreparedStatement
import vdi.component.db.cache.util.withResults
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