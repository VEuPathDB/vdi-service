package vdi.lib.db.app.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setDateTime

private fun sql(schema: String) =
// language=oracle
"""
UPDATE
  ${schema}.sync_control
SET
  data_update_time = ?
WHERE
  dataset_id = ?
"""

internal fun Connection.updateSyncControlDataTimestamp(
  schema: String,
  datasetID: DatasetID,
  timestamp: OffsetDateTime
) {
  withPreparedUpdate(sql(schema)) {
    setDateTime(1, timestamp)
    setDatasetID(2, datasetID)
  }
}
