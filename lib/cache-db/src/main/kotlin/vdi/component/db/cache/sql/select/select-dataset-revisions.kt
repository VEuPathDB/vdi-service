package vdi.component.db.cache.sql.select

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevisionAction
import vdi.component.db.jdbc.getDatasetID
import vdi.component.db.jdbc.setDatasetID
import java.sql.Connection
import java.sql.ResultSet

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
        FROM dataset_revisions
        WHERE revision_id = ?
      )
  )
(TABLE try1)
UNION ALL
(TABLE try2)
ORDER BY timestamp
"""

fun Connection.selectDatasetRevisions(datasetID: DatasetID) =
  executeQuery(datasetID, false) { map {
    VDIDatasetRevision(
      revisionID   = getDatasetID("revision_id"),
      action       = VDIDatasetRevisionAction.fromID(it["action"]),
      timestamp    = it["timestamp"],
      revisionNote = ""
    )
  } }

fun Connection.selectLatestDatasetRevision(datasetID: DatasetID) =
  executeQuery(datasetID, false) {
    if (next())
      VDIDatasetRevision(
        revisionID   = getDatasetID("revision_id"),
        action       = VDIDatasetRevisionAction.fromID(get("action")),
        timestamp    = get("timestamp"),
        revisionNote = ""
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
