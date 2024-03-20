package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import vdi.component.db.app.model.DatasetVisibilityRecord
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  user_id
FROM
  ${schema}.dataset_visibility
WHERE
  dataset_id = ?
"""

internal fun Connection.selectDatasetVisibilityRecords(
  schema: String,
  datasetID: DatasetID
): List<DatasetVisibilityRecord> =
  prepareStatement(sql(schema)).use { ps ->
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