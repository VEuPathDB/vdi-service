package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import java.sql.Connection

// language=oracle
private const val SQL = """
INSERT INTO
  vdi.sync_control (
    dataset_id
  , shares_update_time
  , data_update_time
  , meta_update_time
  )
VALUES
  (?, ?, ?, ?)
"""

internal fun Connection.insertDatasetSyncControl(sync: VDISyncControlRecord) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setString(1, sync.datasetID.toString())
      ps.setObject(2, sync.sharesUpdated)
      ps.setObject(3, sync.dataUpdated)
      ps.setObject(4, sync.metaUpdated)
      ps.executeUpdate()
    }
}