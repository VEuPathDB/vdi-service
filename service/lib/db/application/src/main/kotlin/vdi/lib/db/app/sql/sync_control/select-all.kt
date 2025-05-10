package vdi.lib.db.app.sql.sync_control

import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getDateTime
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.model.ReconcilerTargetRecord

private fun sql(schema: String) =
// language=oracle
"""
SELECT
  s.shares_update_time
, s.data_update_time
, s.meta_update_time
, s.dataset_id
, d.type_name
, d.type_version
, d.owner
, d.is_deleted
FROM
  ${schema}.sync_control s
  INNER JOIN ${schema}.dataset d
    ON d.dataset_id = s.dataset_id
ORDER BY CONCAT(CONCAT(d.owner,'/'), s.dataset_id)
"""

internal fun Connection.selectAllSyncControl(schema: String): CloseableIterator<ReconcilerTargetRecord> {
  val ps = prepareStatement(sql(schema))
  return RecordIterator(ps.executeQuery(), this, ps)
}

class RecordIterator(
  private val rs: ResultSet,
  private val con: Connection,
  private val ps: PreparedStatement
): CloseableIterator<ReconcilerTargetRecord> {

  override fun hasNext(): Boolean {
    return rs.next()
  }

  override fun next(): ReconcilerTargetRecord {
    return ReconcilerTargetRecord(
      datasetID     = rs.reqDatasetID("dataset_id"),
      ownerID       = rs.getUserID("owner"),
      sharesUpdated = rs.getDateTime("shares_update_time"),
      dataUpdated   = rs.getDateTime("data_update_time"),
      metaUpdated   = rs.getDateTime("meta_update_time"),
      type          = VDIDatasetType(rs.getDataType("type_name"), rs.getString("type_version")),
      isUninstalled = rs.getInt("is_deleted") == DeleteFlag.DeletedAndUninstalled.value
    )
  }

  override fun close() {
    rs.close()
    con.close()
    ps.close()
  }
}
