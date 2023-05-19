package org.veupathdb.vdi.lib.db.cache.sql.insert

import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
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
ON CONFLICT (dataset_id)
  DO NOTHING
"""

internal fun Connection.tryInsertSyncControl(record: VDISyncControlRecord) {
  prepareStatement(SQL).use { ps ->
    ps.setDatasetID(1, record.datasetID)
    ps.setObject(2, record.sharesUpdated)
    ps.setObject(3, record.dataUpdated)
    ps.setObject(4, record.metaUpdated)
    ps.execute()
  }
}