package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import java.time.OffsetDateTime

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.sync_control
SET
  meta_update_time = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateSyncControlMetaTimestamp(
  schema: String,
  datasetID: DatasetID,
  timestamp: OffsetDateTime
) {
  prepareStatement(sql(schema))
    .use { ps ->
      ps.setObject(1, timestamp)
      ps.setString(2, datasetID.toString())
      ps.executeUpdate()
    }
}