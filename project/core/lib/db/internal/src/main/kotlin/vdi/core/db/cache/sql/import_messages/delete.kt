package vdi.core.db.cache.sql.import_messages

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteImportMessages(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }
