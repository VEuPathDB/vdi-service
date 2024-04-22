package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.cache.util.getDateTime
import vdi.component.db.cache.util.setDatasetID
import vdi.component.db.cache.util.withPreparedStatement
import vdi.component.db.cache.util.withResults
import java.sql.Connection

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
        VDISyncControlRecord(
          datasetID     = datasetID,
          sharesUpdated = getDateTime("shares_update_time"),
          dataUpdated   = getDateTime("data_update_time"),
          metaUpdated   = getDateTime("meta_update_time")
        )
    }
  }
