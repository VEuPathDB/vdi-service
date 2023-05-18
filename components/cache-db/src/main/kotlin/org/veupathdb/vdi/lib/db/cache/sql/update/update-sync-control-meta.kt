package org.veupathdb.vdi.lib.db.cache.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import java.time.OffsetDateTime

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
 *
 * @return `true` if one or more rows were updated, `false` if no rows were
 * updated.
 *
 * `false` indicates that either the target dataset sync control record does not
 * exist, or that the given [timestamp] value was before or equal to the value
 * that was already set in the existing sync control record.
 */
internal fun Connection.updateSyncControlMeta(datasetID: DatasetID, timestamp: OffsetDateTime): Boolean {
  prepareStatement(SQL).use { ps ->
    ps.setObject(1, timestamp)
    ps.setString(2, datasetID.toString())
    ps.setObject(3, timestamp)
    return ps.executeUpdate() > 0
  }
}