package vdi.core.db.app.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.core.db.model.SyncControlRecord

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

internal fun Connection.insertDatasetSyncControl(schema: String, sync: SyncControlRecord) {
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, sync.datasetID)
    setDateTime(2, sync.sharesUpdated)
    setDateTime(3, sync.dataUpdated)
    setDateTime(4, sync.metaUpdated)
  }
}
