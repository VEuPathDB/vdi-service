package vdi.core.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setUserID

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
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)
    setUserID(2, userID)
    withResults { next() }
  }
