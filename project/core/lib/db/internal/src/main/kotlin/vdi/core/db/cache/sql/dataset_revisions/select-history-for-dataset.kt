package vdi.core.db.cache.sql.dataset_revisions

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision
import vdi.model.meta.DatasetRevisionHistory

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

fun Connection.selectRevisionHistory(datasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, datasetID)
    setDatasetID(2, datasetID)

    withResults {
      var originalID: DatasetID? = null

      val records = map {
        if (originalID == null)
          originalID = reqDatasetID("original_id")

        DatasetRevision(
          revisionID   = reqDatasetID("revision_id"),
          action       = DatasetRevision.Action.fromID(it.getInt("action").toUByte()),
          timestamp    = it["timestamp"],

          // revisionNote is not stored in the cache DB and must be looked up
          // from the meta json file later.
          revisionNote = "",
        )
      }

      originalID?.let { DatasetRevisionHistory(it, records) }
    }
  }