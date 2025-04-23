package vdi.lib.db.cache.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.util.setImportStatus
import vdi.lib.db.jdbc.setDatasetID
import java.sql.Connection

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
