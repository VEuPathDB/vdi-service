package vdi.component.db.app.sql

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetTypeImpl
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.app.model.DeleteFlag
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime

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

internal fun Connection.selectAllSyncControl(schema: String): CloseableIterator<VDIReconcilerTargetRecord> {
  val ps = prepareStatement(sql(schema))
  val rs = ps.executeQuery()
  return RecordIterator(rs, this, ps)
}

class RecordIterator(val rs: ResultSet,
                     private val con: Connection,
                     private val ps: PreparedStatement): CloseableIterator<VDIReconcilerTargetRecord> {

  override fun hasNext(): Boolean {
    return rs.next();
  }

  override fun next(): VDIReconcilerTargetRecord {
    return VDIReconcilerTargetRecord(
      datasetID = DatasetID(rs.getString("dataset_id")),
      ownerID = UserID(rs.getLong("owner")),
      sharesUpdated = rs.getObject("shares_update_time", OffsetDateTime::class.java),
      dataUpdated = rs.getObject("data_update_time", OffsetDateTime::class.java),
      metaUpdated = rs.getObject("meta_update_time", OffsetDateTime::class.java),
      type=VDIDatasetTypeImpl(
        name = rs.getString("type_name"),
        version = rs.getString("type_version")
      ),
      isUninstalled = rs.getInt("is_deleted") == DeleteFlag.DeletedAndUninstalled.value
    )
  }

  override fun close() {
    rs.close()
    con.close()
    ps.close()
  }
}