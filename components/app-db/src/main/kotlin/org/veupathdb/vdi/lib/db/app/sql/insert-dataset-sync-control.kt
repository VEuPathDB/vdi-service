package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
INSERT INTO
  ${schema}.sync_control (
    dataset_id
  , shares_update_time
  , data_update_time
  , meta_update_time
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertDatasetSyncControl(schema: String, sync: VDISyncControlRecord) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setString(1, sync.datasetID.toString())
      ps.setObject(2, sync.sharesUpdated)
      ps.setObject(3, sync.dataUpdated)
      ps.setObject(4, sync.metaUpdated)
      ps.executeUpdate()
    }
}