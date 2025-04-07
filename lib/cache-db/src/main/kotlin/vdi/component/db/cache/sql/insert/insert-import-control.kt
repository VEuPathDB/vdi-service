package vdi.component.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.util.setImportStatus
import vdi.component.db.jdbc.setDatasetID
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
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertImportControl(datasetID: DatasetID, status: DatasetImportStatus) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setImportStatus(2, status)
  }
