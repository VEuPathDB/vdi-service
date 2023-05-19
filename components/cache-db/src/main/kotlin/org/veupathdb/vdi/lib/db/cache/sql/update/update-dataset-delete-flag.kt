package org.veupathdb.vdi.lib.db.cache.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.db.cache.util.preparedUpdate
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

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
  preparedUpdate(SQL) {
    setBoolean(1, deleted)
    setDatasetID(2, datasetID)
  }
