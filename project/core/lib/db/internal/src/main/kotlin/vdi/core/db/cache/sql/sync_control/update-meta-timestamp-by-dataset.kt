package vdi.core.db.cache.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import vdi.model.meta.DatasetID
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime

// language=postgresql
private const val SQL = """
UPDATE
  vdi.sync_control
SET
  meta_update_time = ?
WHERE
  dataset_id = ?
  AND meta_update_time < ?
"""

/**
 * Updates the sync control record for a target dataset setting the meta update
 * time to the given new value only if the new value is more recent than the
 * value presently in the database.
 *
 * @param datasetID ID of the target dataset whose sync control record should
 * be updated.
 *
 * @param timestamp New timestamp for the meta field in the target sync control
 * record.
 */
internal fun Connection.updateSyncControlMeta(datasetID: DatasetID, timestamp: OffsetDateTime) =
  withPreparedUpdate(SQL) {
    setDateTime(1, timestamp)
    setDatasetID(2, datasetID)
    setDateTime(3, timestamp)
  }
