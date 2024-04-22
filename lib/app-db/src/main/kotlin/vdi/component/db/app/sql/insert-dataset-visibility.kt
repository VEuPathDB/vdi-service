package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.dataset_visibility (
    dataset_id
  , user_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetVisibility(schema: String, datasetID: DatasetID, userID: UserID) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setLong(2, userID.toLong())
      ps.executeUpdate()
    }
}