package vdi.component.db.cache.sql.upsert

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import java.sql.Connection

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
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, status.value)
    setString(3, status.value)
  }
