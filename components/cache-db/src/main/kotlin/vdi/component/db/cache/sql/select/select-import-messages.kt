package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.map
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.withPreparedStatement
import vdi.component.db.cache.util.withResults
import java.sql.Connection

// language=postgresql
private const val SQL = """
SELECT
  message
FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

internal fun Connection.selectImportMessages(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults { map { it.getString(1) } }
  }
