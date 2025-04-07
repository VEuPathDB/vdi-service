package vdi.component.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.jdbc.setDatasetID
import vdi.component.db.jdbc.setDateTime
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

internal fun Connection.tryInsertSyncControl(record: VDISyncControlRecord) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, record.datasetID)
    setDateTime(2, record.sharesUpdated)
    setDateTime(3, record.dataUpdated)
    setDateTime(4, record.metaUpdated)
  }
