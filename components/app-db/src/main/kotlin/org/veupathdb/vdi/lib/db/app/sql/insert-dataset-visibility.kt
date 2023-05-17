package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.dataset_visibility (
    dataset_id
  , user_id
  )
VALUES
  (?, ?)
"""

internal fun Connection.insertDatasetVisibility(datasetID: DatasetID, userID: UserID) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, datasetID.toString())
      ps.setLong(2, userID.toLong())
      ps.executeUpdate()
    }
}