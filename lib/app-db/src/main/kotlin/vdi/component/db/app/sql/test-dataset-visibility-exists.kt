package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  1
FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
  AND user_id = ?
"""

internal fun Connection.testDatasetVisibilityExists(schema: String, datasetID: DatasetID, userID: UserID): Boolean =
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setDatasetID(1, datasetID)
      ps.setUserID(2, userID)
      ps.executeQuery().use { rs -> rs.next() }
    }
