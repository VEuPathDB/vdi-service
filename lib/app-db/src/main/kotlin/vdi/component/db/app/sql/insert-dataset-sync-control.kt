package vdi.component.db.app.sql

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
  preparedUpdate(sql(schema)) {
    setDatasetID(1, sync.datasetID)
    setDateTime(2, sync.sharesUpdated)
    setDateTime(3, sync.dataUpdated)
    setDateTime(4, sync.metaUpdated)
  }
}
