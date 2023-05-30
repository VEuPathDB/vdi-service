package org.veupathdb.vdi.lib.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime

// language=postgresql
private const val SQL = """
SELECT
  shares_update_time
, data_update_time
, meta_update_time
, dataset_id
FROM
  vdi.sync_control
ORDER BY dataset_id
"""

internal fun Connection.selectAllSyncControl(): CloseableIterator<VDISyncControlRecord> {
  val ps = prepareStatement(SQL)
  val rs = ps.executeQuery()
  return RecordIterator(rs, this, ps)
}

class RecordIterator(val rs: ResultSet, val connection: Connection, val preparedStatement: PreparedStatement): CloseableIterator<VDISyncControlRecord> {

  override fun hasNext(): Boolean {
    return rs.next();
  }

  override fun next(): VDISyncControlRecord {
    return VDISyncControlRecord(
      datasetID     = DatasetID(rs.getString("dataset_id")),
      sharesUpdated = rs.getObject("shares_update_time", OffsetDateTime::class.java),
      dataUpdated   = rs.getObject("data_update_time", OffsetDateTime::class.java),
      metaUpdated   = rs.getObject("meta_update_time", OffsetDateTime::class.java)
    )
  }

  override fun close() {
    rs.close()
    connection.close()
    preparedStatement.close()
  }
}