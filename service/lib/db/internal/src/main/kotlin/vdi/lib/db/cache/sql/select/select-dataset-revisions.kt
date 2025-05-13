package vdi.lib.db.cache.sql.select

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import java.sql.Connection
import java.sql.ResultSet
import vdi.lib.db.cache.model.DatasetRevisionRecord
import vdi.lib.db.cache.model.DatasetRevisionRecordSet
import vdi.lib.db.jdbc.reqDatasetID
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.logging.logger

/**
 * This slightly wonky looking query attempts to locate the revision records for
 * a target dataset given either the original dataset ID, or the ID of another
 * revision of that dataset.
 *
 * Try 1 attempts to find records under the assumption that the given ID is the
 * original dataset ID.  If no such records are found, Try 2 is evaluated.
 *
 * Try 2 is lazily evaluated only if Try 1 finds no records, and assumes the
 * given ID is the ID of a revision of the target dataset.  The query gets the
 * original dataset ID by looking it up using the given dataset ID, then using
 * the retrieved original ID, fetches the revisions for that original ID.
 */
// language=postgresql
private const val SQL = """
WITH
  try1 AS (
    SELECT
      revision_id
    , original_id
    , action
    , timestamp
    FROM
      vdi.dataset_revisions
    WHERE
      original_id = ?
  )
-- lazy evaluate, try2 is not executed if try1 returns a result
, try2 AS (
    SELECT
      revision_id
    , original_id
    , action
    , timestamp
    FROM
      vdi.dataset_revisions
    WHERE
      NOT EXISTS (TABLE try1)
      AND original_id = (
        SELECT original_id
        FROM vdi.dataset_revisions
        WHERE revision_id = ?
      )
  )
(TABLE try1)
UNION ALL
(TABLE try2)
ORDER BY timestamp DESC
"""

fun Connection.selectDatasetRevisions(datasetID: DatasetID) =
  executeQuery(datasetID, false) {
    var originalID: DatasetID? = null
    val records = map {
      if (originalID == null)
        originalID = reqDatasetID("original_id")

      VDIDatasetRevision(
        revisionID   = reqDatasetID("revision_id"),
        action       = VDIDatasetRevisionAction.fromID(it.getInt("action").toUByte()),
        timestamp    = it["timestamp"],
        revisionNote = ""
      )
    }

    originalID?.let { DatasetRevisionRecordSet(it, records) }
  }

fun Connection.selectLatestDatasetRevision(datasetID: DatasetID) =
  executeQuery(datasetID, true) {
    if (next())
      DatasetRevisionRecord(
        revisionID   = reqDatasetID("revision_id"),
        action       = VDIDatasetRevisionAction.fromID(getInt("action").toUByte()),
        timestamp    = get("timestamp"),
        revisionNote = "",
        originalID   = reqDatasetID("original_id"),
      )
    else
      null
  }

private inline fun <T> Connection.executeQuery(datasetID: DatasetID, limit: Boolean, fn: ResultSet.() -> T) =
  withPreparedStatement(if (limit) "$SQL\nLIMIT 1" else SQL) {
    setDatasetID(1, datasetID)
    setDatasetID(2, datasetID)
    withResults(fn)
  }
