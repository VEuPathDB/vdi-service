package vdi.core.db.app.sql.sync_control

import io.foxcapades.kdbc.withPreparedUpdate
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.model.data.DatasetID

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
