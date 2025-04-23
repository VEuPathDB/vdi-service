package vdi.lib.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.import_messages
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteImportMessages(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) }
