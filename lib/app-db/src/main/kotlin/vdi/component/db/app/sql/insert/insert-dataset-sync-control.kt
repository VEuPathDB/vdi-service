package vdi.component.db.app.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.jdbc.setDatasetID
import vdi.component.db.jdbc.setDateTime
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
  withPreparedUpdate(sql(schema)) {
    setDatasetID(1, sync.datasetID)
    setDateTime(2, sync.sharesUpdated)
    setDateTime(3, sync.dataUpdated)
    setDateTime(4, sync.metaUpdated)
  }
}
