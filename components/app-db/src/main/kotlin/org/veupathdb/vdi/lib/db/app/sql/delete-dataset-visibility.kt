package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

// language=oracle
private const val SQL = """
DELETE FROM
  vdi.dataset_visibility
WHERE
  dataset_id = ?
  AND user_id = ?
"""

internal fun Connection.deleteDatasetVisibility(datasetID: DatasetID, userID: UserID): Int =
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setLong(2, userID.toLong())
      ps.executeUpdate()
    }