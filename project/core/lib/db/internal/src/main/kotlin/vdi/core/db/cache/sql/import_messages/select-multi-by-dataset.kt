package vdi.core.db.cache.sql.import_messages

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.meta.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

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
    withResults { map { it.getString(1)!! } }
  }
