package vdi.core.db.cache.sql.select

import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import java.sql.Connection
import vdi.core.db.jdbc.getDateTime
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.model.SyncControlRecord

// language=postgresql
private const val SQL = """
SELECT
  shares_update_time
, data_update_time
, meta_update_time
FROM
  vdi.sync_control
WHERE
  dataset_id = ?
"""

internal fun Connection.selectSyncControl(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    withResults {
      if (!next())
        null
      else
        SyncControlRecord(
          datasetID     = datasetID,
          sharesUpdated = getDateTime("shares_update_time"),
          dataUpdated   = getDateTime("data_update_time"),
          metaUpdated   = getDateTime("meta_update_time")
        )
    }
  }
