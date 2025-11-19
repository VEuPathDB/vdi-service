package vdi.core.db.app.sql.sync_control

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.model.SyncControlRecord
import vdi.model.meta.DatasetID

private fun sql(schema: String) =
// language=postgresql
"""
SELECT
  shares_update_time
, data_update_time
, meta_update_time
FROM
  ${schema}.${Table.SyncControl}
WHERE
  dataset_id = ?
"""

internal fun Connection.selectSyncControl(schema: String, datasetID: DatasetID) =
  withPreparedStatement(sql(schema)) {
    setDatasetID(1, datasetID)

    withResults {
      if (!next())
        null
      else
        SyncControlRecord(
          datasetID     = datasetID,
          sharesUpdated = get("shares_update_time"),
          dataUpdated   = get("data_update_time"),
          metaUpdated   = get("meta_update_time"),
        )
    }
  }
