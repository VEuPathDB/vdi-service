package vdi.core.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.util.setImportStatus
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
UPDATE
  vdi.import_control
SET
  status = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetImportStatus(datasetID: DatasetID, syncStatus: DatasetImportStatus) =
  withPreparedUpdate(SQL) {
    setImportStatus(1, syncStatus)
    setDatasetID(2, datasetID)
  }
