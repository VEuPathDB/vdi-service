package vdi.lib.db.app.sql.sync_control

import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import vdi.lib.db.app.AppDBPlatform
import vdi.lib.db.app.model.DeleteFlag
import vdi.lib.db.jdbc.getDataType
import vdi.lib.db.jdbc.getDateTime
import vdi.lib.db.jdbc.getUserID
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.model.ReconcilerTargetRecord

private fun sql(schema: String, strpos: String) =
// language=oracle
"""
WITH results AS (
  SELECT
    s.shares_update_time
  , s.data_update_time
  , s.meta_update_time
  , s.dataset_id
  , d.type_name
  , d.type_version
  , d.owner
  , d.is_deleted
  -- Sort ID is needed to align the result rows with the object key stream
  -- coming from the object store
  , d.owner || '/' || (
    CASE
      WHEN ${strpos}(d.dataset_id, '.') > 0
        THEN d.dataset_id
      ELSE
        d.dataset_id || '.z'
    END
  ) AS sort_id
  FROM
    ${schema}.sync_control s
    INNER JOIN ${schema}.dataset d
      ON s.dataset_id = d.dataset_id
)
SELECT
  shares_update_time
, data_update_time
, meta_update_time
, dataset_id
, type_name
, type_version
, owner
, is_deleted
FROM results
ORDER BY sort_id
"""

private const val PostgresStrPos = "strpos"
private const val OracleStrPos = "instr"

internal fun Connection.selectAllSyncControl(schema: String, platform: AppDBPlatform): CloseableIterator<ReconcilerTargetRecord> {
  val ps = prepareStatement(sql(schema, when (platform) {
    AppDBPlatform.Postgres -> PostgresStrPos
    AppDBPlatform.Oracle   -> OracleStrPos
  }))
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
