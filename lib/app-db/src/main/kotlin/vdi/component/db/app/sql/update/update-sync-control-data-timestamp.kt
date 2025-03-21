package vdi.component.db.app.sql.update

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.app.sql.preparedUpdate
import vdi.component.db.app.sql.setDatasetID
import vdi.component.db.app.sql.setDateTime
import java.sql.Connection
import java.time.OffsetDateTime

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
  preparedUpdate(sql(schema)) {
    setDateTime(1, timestamp)
    setDatasetID(2, datasetID)
  }
}
