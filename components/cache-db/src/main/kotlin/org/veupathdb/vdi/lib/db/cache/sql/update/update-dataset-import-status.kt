package org.veupathdb.vdi.lib.db.cache.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
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
  preparedUpdate(SQL) {
    setString(1, syncStatus.value)
    setDatasetID(2, datasetID)
  }
