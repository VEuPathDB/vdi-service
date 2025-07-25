package vdi.core.db.cache.sql.import_control

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.util.setImportStatus
import vdi.core.db.jdbc.setDatasetID

// language=postgresql
private const val SQL = """
INSERT INTO
  vdi.import_control (
    dataset_id
  , status
  )
VALUES
  (?, ?)
ON CONFLICT (dataset_id) DO UPDATE
SET
  status = ?
"""

internal fun Connection.upsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setImportStatus(2, status)
    setImportStatus(3, status)
  }
