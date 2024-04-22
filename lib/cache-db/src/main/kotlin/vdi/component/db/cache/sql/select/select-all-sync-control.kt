package vdi.component.db.cache.sql.select

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.model.VDIDatasetTypeImpl
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.util.getDatasetID
import vdi.component.db.cache.util.getDateTime
import vdi.component.db.cache.util.getUserID
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
, d.owner_id
, d.is_deleted
, d.inserted -- FIXME: part of the temp hack
, ic.status  -- FIXME: part of the temp hack
FROM
  vdi.sync_control AS s
  INNER JOIN vdi.datasets AS d
    USING (dataset_id)
  LEFT JOIN vdi.import_control AS ic -- FIXME: part of the temp hack
    USING (dataset_id)
ORDER BY CONCAT(d.owner_id,'/',s.dataset_id)
"""

internal fun Connection.selectAllSyncControl(): CloseableIterator<VDIReconcilerTargetRecord> {
  val ps = prepareStatement(SQL)
  val rs = ps.executeQuery()
  return RecordIterator(rs, this, ps)
}

// FIXME: Remove this class when the target db delete logic is moved from the
//        reconciler to the hard-delete lane.
class TempHackCacheDBReconcilerTargetRecord(
  override val isUninstalled: Boolean,
  override val ownerID: UserID,
  override val type: VDIDatasetType,
  override val dataUpdated: OffsetDateTime,
  override val datasetID: DatasetID,
  override val metaUpdated: OffsetDateTime,
  override val sharesUpdated: OffsetDateTime,

  val inserted: OffsetDateTime,
  val importStatus: DatasetImportStatus,
) : VDIReconcilerTargetRecord {
  override fun getComparableID() = "$ownerID/$datasetID"
}

class RecordIterator(val rs: ResultSet,
                     val connection: Connection,
                     val preparedStatement: PreparedStatement): CloseableIterator<VDIReconcilerTargetRecord> {

  override fun hasNext(): Boolean {
    return rs.next();
  }

  override fun next(): VDIReconcilerTargetRecord {
    // FIXME: Remove this when the target db delete logic is moved from the
    //        reconciler to the hard-delete lane.
    return TempHackCacheDBReconcilerTargetRecord(
      ownerID = rs.getUserID("owner_id"),
      datasetID = rs.getDatasetID("dataset_id"),
      sharesUpdated = rs.getDateTime("shares_update_time"),
      dataUpdated = rs.getDateTime("data_update_time"),
      metaUpdated = rs.getDateTime("meta_update_time"),
      type = VDIDatasetTypeImpl(
        name = rs.getString("type_name"),
        version = rs.getString("type_version")
      ),
      isUninstalled = rs.getBoolean("is_deleted"),
      inserted = rs.getDateTime("inserted"),
      importStatus = rs.getString("status")?.let(DatasetImportStatus::fromString) ?: DatasetImportStatus.Queued
    )

// FIXME: Uncomment the following when the target db delete logic is moved from
//        the reconciler to the hard-delete lane.
//
//    return VDIReconcilerTargetRecord(
//      ownerID = rs.getUserID("owner_id"),
//      datasetID = rs.getDatasetID("dataset_id"),
//      sharesUpdated = rs.getDateTime("shares_update_time"),
//      dataUpdated = rs.getDateTime("data_update_time"),
//      metaUpdated = rs.getDateTime("meta_update_time"),
//      type = VDIDatasetTypeImpl(
//        name = rs.getString("type_name"),
//        version = rs.getString("type_version")
//      ),
//      isUninstalled = rs.getBoolean("is_deleted"),
//    )
  }

  override fun close() {
    rs.close()
    connection.close()
    preparedStatement.close()
  }
}