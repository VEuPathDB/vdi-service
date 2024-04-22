package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
DELETE FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
  AND user_id = ?
"""

internal fun Connection.deleteDatasetVisibility(schema: String, datasetID: DatasetID, userID: UserID): Int =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setLong(2, userID.toLong())
      ps.executeUpdate()
    }