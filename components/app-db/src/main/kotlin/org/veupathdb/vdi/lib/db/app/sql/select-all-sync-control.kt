package org.veupathdb.vdi.lib.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIDatasetTypeImpl
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime

// language=postgresql
private const val SQL = """
SELECT
  s.shares_update_time
, s.data_update_time
, s.meta_update_time
, s.dataset_id
, d.type_name
, d.type_version
FROM
  vdi.sync_control s
  INNER JOIN vdi.dataset d
    ON d.dataset_id = s.dataset_id
ORDER BY CONCAT(d.owner,'/',s.dataset_id)
"""

internal fun Connection.selectAllSyncControl(): CloseableIterator<Pair<VDIDatasetType, VDISyncControlRecord>> {
  val ps = prepareStatement(SQL)
  val rs = ps.executeQuery()
  return RecordIterator(rs, this, ps)
}

class RecordIterator(val rs: ResultSet,
                     private val con: Connection,
                     private val ps: PreparedStatement): CloseableIterator<Pair<VDIDatasetType, VDISyncControlRecord>> {

  override fun hasNext(): Boolean {
    return rs.next();
  }

  override fun next(): Pair<VDIDatasetType, VDISyncControlRecord> {
    return Pair(
      VDIDatasetTypeImpl(
        name = rs.getString("type_name"),
        version = rs.getString("type_version")
      ), VDISyncControlRecord(
        datasetID = DatasetID(rs.getString("dataset_id")),
        sharesUpdated = rs.getObject("shares_update_time", OffsetDateTime::class.java),
        dataUpdated = rs.getObject("data_update_time", OffsetDateTime::class.java),
        metaUpdated = rs.getObject("meta_update_time", OffsetDateTime::class.java)
      )
    )
  }

  override fun close() {
    rs.close()
    con.close()
    ps.close()
  }
}