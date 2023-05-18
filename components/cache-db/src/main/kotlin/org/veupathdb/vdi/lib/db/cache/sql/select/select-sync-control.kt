package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import java.sql.Connection
import java.time.OffsetDateTime

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

internal fun Connection.selectSyncControl(datasetID: DatasetID): VDISyncControlRecord? {
  prepareStatement(SQL).use { ps ->
    ps.setString(1, datasetID.toString())

    ps.executeQuery().use { rs ->
      if (!rs.next())
        return null

      return VDISyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = rs.getObject("shares_update_time", OffsetDateTime::class.java),
        dataUpdated   = rs.getObject("data_update_time", OffsetDateTime::class.java),
        metaUpdated   = rs.getObject("meta_update_time", OffsetDateTime::class.java)
      )
    }
  }
}