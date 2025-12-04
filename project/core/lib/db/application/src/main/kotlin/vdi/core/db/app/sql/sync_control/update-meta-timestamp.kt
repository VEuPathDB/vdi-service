package vdi.core.db.app.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import java.sql.SQLException
import java.time.OffsetDateTime
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.core.db.model.SyncControlRecord
import vdi.db.app.TargetDBPlatform
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetID

private fun updateSQL(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.${Table.SyncControl}
SET
  meta_update_time = ?
WHERE
  dataset_id = ?
"""

private fun upsertSQL(schema: String) =
// language=postgresql
"""
INSERT INTO
  ${schema}.${Table.SyncControl} (
    dataset_id
  , shares_update_time
  , data_update_time
  , meta_update_time
  )
VALUES
  (?, ?, ?, ?)
ON CONFLICT (dataset_id)
  DO UPDATE
  SET meta_update_time = ?
"""

internal fun Connection.upsertSyncControlMetaTimestamp(
  platform:  TargetDBPlatform,
  schema:    String,
  datasetID: DatasetID,
  timestamp: OffsetDateTime,
) =
  when (platform) {
    TargetDBPlatform.Postgres -> withPreparedUpdate(upsertSQL(schema)) {
      setDatasetID(1, datasetID)
      setDateTime(2, OriginTimestamp) // shares
      setDateTime(3, OriginTimestamp) // data
      setDateTime(4, timestamp)       // meta
      setDateTime(5, timestamp)       // update meta
    }

    TargetDBPlatform.Oracle -> {
      try {
        insertDatasetSyncControl(schema, SyncControlRecord(datasetID, metaUpdated = timestamp))
      } catch (e: SQLException) {
        if (!platform.isUniqueConstraintViolation(e)) throw e
        withPreparedUpdate(updateSQL(schema)) {
          setDateTime(1, timestamp)
          setDatasetID(2, datasetID)
        }
      }
    }
  }
