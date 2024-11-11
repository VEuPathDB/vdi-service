package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
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
    ps.setDatasetID(1, datasetID)
    ps.executeQuery().use { rs ->
      val out = ArrayList<DatasetVisibilityRecord>()

      while (rs.next()) {
        out.add(DatasetVisibilityRecord(datasetID, rs.getUserID("user_id")))
      }

      out
    }
  }
