package vdi.lib.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_metadata
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetMetadata(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) } > 0
