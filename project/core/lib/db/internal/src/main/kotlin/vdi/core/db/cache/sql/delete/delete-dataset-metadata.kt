package vdi.core.db.cache.sql.delete

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_metadata
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetMetadata(datasetID: DatasetID) =
  withPreparedUpdate(SQL) { setDatasetID(1, datasetID) } > 0
