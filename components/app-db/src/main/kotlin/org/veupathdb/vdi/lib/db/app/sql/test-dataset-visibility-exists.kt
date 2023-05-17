package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  1
FROM
  vdi.dataset_visibility
WHERE
  dataset_id = ?
  AND user_id = ?
"""

internal fun Connection.testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID): Boolean =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setLong(2, userID.toLong())
      ps.executeQuery()
        .use { rs -> rs.next() }
    }