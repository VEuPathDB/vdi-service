package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.db.app.model.DatasetVisibilityRecord
import java.sql.Connection

// language=oracle
private const val SQL = """
SELECT
  user_id
FROM
  vdi.dataset_visibility
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetVisibilityRecords(datasetID: DatasetID): List<DatasetVisibilityRecord> =
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())
    ps.executeQuery().use { rs ->
      val out = ArrayList<DatasetVisibilityRecord>()

      while (rs.next()) {
        out.add(DatasetVisibilityRecord(
          datasetID = datasetID,
          userID    = UserID(rs.getLong("user_id"))
        ))
      }

      out
    }
  }