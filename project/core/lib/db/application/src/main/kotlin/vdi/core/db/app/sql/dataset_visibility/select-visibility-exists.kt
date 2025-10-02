package vdi.core.db.app.sql.dataset_visibility

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.app.sql.setUserID
import vdi.model.data.DatasetID
import vdi.model.data.UserID

private fun sql(schema: String) =
// language=postgresql
"""
SELECT
  1
FROM
  ${schema}.${Table.Visibility}
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
