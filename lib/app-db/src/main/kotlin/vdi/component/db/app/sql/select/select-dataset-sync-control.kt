package vdi.component.db.app.sql.select

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  shares_update_time
, data_update_time
, meta_update_time
FROM
  ${schema}.sync_control
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
        VDISyncControlRecord(
          datasetID     = datasetID,
          sharesUpdated = get("shares_update_time"),
          dataUpdated   = get("data_update_time"),
          metaUpdated   = get("meta_update_time"),
        )
    }
  }
