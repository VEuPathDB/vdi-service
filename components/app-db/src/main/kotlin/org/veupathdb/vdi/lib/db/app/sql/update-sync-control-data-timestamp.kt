package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import java.time.OffsetDateTime

// language=oracle
private const val SQL = """
UPDATE
  vdi.sync_control
SET
  data_update_time = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
  prepareStatement(SQL)
    .use { ps ->
      ps.setObject(1, timestamp)
      ps.setString(2, datasetID.toString())
      ps.executeUpdate()
    }
}