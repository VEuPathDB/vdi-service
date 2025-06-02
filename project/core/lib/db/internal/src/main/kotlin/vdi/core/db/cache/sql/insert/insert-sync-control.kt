package vdi.core.db.cache.sql.insert

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.core.db.model.SyncControlRecord

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

internal fun Connection.tryInsertSyncControl(record: SyncControlRecord) =
  withPreparedUpdate(SQL) {
    setDatasetID(1, record.datasetID)
    setDateTime(2, record.sharesUpdated)
    setDateTime(3, record.dataUpdated)
    setDateTime(4, record.metaUpdated)
  }
