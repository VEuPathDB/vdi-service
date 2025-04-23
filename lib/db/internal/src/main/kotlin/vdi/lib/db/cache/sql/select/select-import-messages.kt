package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
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
