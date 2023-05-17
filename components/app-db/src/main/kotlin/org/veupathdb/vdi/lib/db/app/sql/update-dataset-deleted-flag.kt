package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

// language=oracle
private const val SQL = """
UPDATE
  vdi.datasets
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetDeletedFlag(datasetID: DatasetID, isDeleted: Boolean) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setBoolean(1, isDeleted)
      ps.setString(2, datasetID.toString())
      ps.executeUpdate()
    }
}