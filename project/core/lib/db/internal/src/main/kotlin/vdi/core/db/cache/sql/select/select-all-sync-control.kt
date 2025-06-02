package vdi.core.db.cache.sql.select

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetType
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.OffsetDateTime
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.util.getImportStatus
import vdi.core.db.jdbc.getDataType
import vdi.core.db.jdbc.getDateTime
import vdi.core.db.jdbc.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.util.io.CloseableIterator

// language=postgresql
private const val SQL = """
WITH results AS (
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
  -- Sort ID is needed to align the result rows with the object key stream
  -- coming from the object store
  , d.owner_id || '/' || (
    CASE
      WHEN strpos(d.dataset_id, '.') > 0
        THEN d.dataset_id
      ELSE
        d.dataset_id || '.z'
    END
  ) AS sort_id
  FROM
    vdi.sync_control AS s
    INNER JOIN vdi.datasets AS d
      USING (dataset_id)
    LEFT JOIN vdi.import_control AS ic -- FIXME: part of the temp hack
      USING (dataset_id)
)
SELECT
  shares_update_time
, data_update_time
, meta_update_time
, dataset_id
, type_name
, type_version
, owner_id
, is_deleted
, inserted
, status
FROM results
ORDER BY sort_id
"""

internal fun Connection.selectAllSyncControl(): CloseableIterator<ReconcilerTargetRecord> {
  val ps = prepareStatement(SQL)
  val rs = ps.executeQuery()
  return RecordIterator(rs, this, ps)
}

// FIXME: Remove this class when the target db delete logic is moved from the
//        reconciler to the hard-delete lane.
class TempHackCacheDBReconcilerTargetRecord(
  ownerID: UserID,
  datasetID: DatasetID,
  sharesUpdated: OffsetDateTime,
  dataUpdated: OffsetDateTime,
  metaUpdated: OffsetDateTime,
  type: DatasetType,
  isUninstalled: Boolean,

  val inserted: OffsetDateTime,
  val importStatus: DatasetImportStatus,
): ReconcilerTargetRecord(ownerID, datasetID, sharesUpdated, dataUpdated, metaUpdated, type, isUninstalled)

class RecordIterator(
  val rs: ResultSet,
  val connection: Connection,
  val preparedStatement: PreparedStatement
): CloseableIterator<ReconcilerTargetRecord> {

  override fun hasNext() = rs.next()

  override fun next(): ReconcilerTargetRecord {
    // FIXME: Remove this when the target db delete logic is moved from the
    //        reconciler to the hard-delete lane.
    return TempHackCacheDBReconcilerTargetRecord(
      ownerID       = rs.getUserID("owner_id"),
      datasetID     = rs.reqDatasetID("dataset_id"),
      sharesUpdated = rs.getDateTime("shares_update_time"),
      dataUpdated   = rs.getDateTime("data_update_time"),
      metaUpdated   = rs.getDateTime("meta_update_time"),
      type          = DatasetType(rs.getDataType("type_name"), rs.getString("type_version")),
      isUninstalled = rs.getBoolean("is_deleted"),
      inserted      = rs.getDateTime("inserted"),
      importStatus  = rs.getImportStatus("status") ?: DatasetImportStatus.Queued
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
//      type = DatasetTypeImpl(
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
