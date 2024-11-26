package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  shares_update_time
, data_update_time
, meta_update_time
FROM
  ${schema}.sync_control
WHERE
  dataset_id = ?
"""

internal fun Connection.selectSyncControl(schema: String, datasetID: DatasetID): VDISyncControlRecord? {
  prepareStatement(sql(schema)).use { ps ->
    ps.setDatasetID(1, datasetID)

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return VDISyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = rs.getDateTime("shares_update_time"),
        dataUpdated   = rs.getDateTime("data_update_time"),
        metaUpdated   = rs.getDateTime("meta_update_time"),
      )
    }
  }
}
