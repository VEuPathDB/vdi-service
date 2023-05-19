package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
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
  preparedUpdate(SQL) {
    setDatasetID(1, datasetID)
    setString(2, status.value)
  }
