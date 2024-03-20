package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.model.DeleteFlag
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

internal fun Connection.updateDatasetDeletedFlag(schema: String, datasetID: DatasetID, deleteFlag: DeleteFlag) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setInt(1, deleteFlag.value)
      ps.setString(2, datasetID.toString())
      ps.executeUpdate()
    }
}