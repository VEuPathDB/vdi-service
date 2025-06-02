package vdi.core.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.datasets
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetDeleteFlag(datasetID: DatasetID, deleted: Boolean) =
  withPreparedUpdate(SQL) {
    setBoolean(1, deleted)
    setDatasetID(2, datasetID)
  }
