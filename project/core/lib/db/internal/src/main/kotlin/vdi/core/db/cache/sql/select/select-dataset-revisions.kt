package vdi.core.db.cache.sql.select

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.data.DatasetID
import vdi.model.data.DatasetRevision
import java.sql.Connection
import vdi.core.db.cache.model.DatasetRevisionRecordSet
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID

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
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    setDatasetID(2, datasetID)

    withResults {
      var originalID: DatasetID? = null
      val records = map {
        if (originalID == null)
          originalID = reqDatasetID("original_id")

        DatasetRevision(
          revisionID = reqDatasetID("revision_id"),
          action = DatasetRevision.Action.fromID(it.getInt("action").toUByte()),
          timestamp = it["timestamp"],
          revisionNote = ""
        )
      }

      originalID?.let { DatasetRevisionRecordSet(it, records) }
    }
  }
