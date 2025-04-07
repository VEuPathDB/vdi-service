package vdi.component.db.app.sql.update

import io.foxcapades.kdbc.withPreparedUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.jdbc.setDatasetID
import vdi.component.db.jdbc.setDateTime
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
  withPreparedUpdate(sql(schema)) {
    setDateTime(1, timestamp)
    setDatasetID(2, datasetID)
  }
}
