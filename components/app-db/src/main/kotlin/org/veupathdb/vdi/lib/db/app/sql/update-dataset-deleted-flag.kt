package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.dataset
SET
  is_deleted = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateDatasetDeletedFlag(schema: String, datasetID: DatasetID, isDeleted: Boolean) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setBoolean(1, isDeleted)
      ps.setString(2, datasetID.toString())
      ps.executeUpdate()
    }
}