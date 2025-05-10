package vdi.lib.db.cache.sql.upsert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.util.setImportStatus
import vdi.lib.db.jdbc.setDatasetID

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
