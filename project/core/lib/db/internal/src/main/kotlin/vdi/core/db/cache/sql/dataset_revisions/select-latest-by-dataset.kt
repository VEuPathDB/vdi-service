package vdi.core.db.cache.sql.dataset_revisions

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision
import java.sql.Connection
import vdi.core.db.cache.model.DatasetRevisionRecord
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID


// language=postgresql
private const val SQL_BEGIN = """
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
, resolved AS (
    (TABLE try1)
    UNION ALL
    (TABLE try2)
  )
SELECT
  r.revision_id
, r.original_id
, r.action
, r.timestamp
FROM
  resolved AS r
"""

private const val SQL_TAIL_EXCLUDE_DELETED = """  INNER JOIN vdi.datasets AS d
    ON r.revision_id = d.dataset_id
WHERE
  d.is_deleted = false
ORDER BY timestamp DESC
LIMIT 1"""

private const val SQL_TAIL_INCLUDE_DELETED = "ORDER BY timestamp DESC\nLIMIT 1"

fun Connection.selectLatestDatasetRevision(datasetID: DatasetID, includeDeleted: Boolean) =
  withPreparedStatement(SQL_BEGIN + if (includeDeleted) SQL_TAIL_INCLUDE_DELETED else SQL_TAIL_EXCLUDE_DELETED) {
    setDatasetID(1, datasetID)
    setDatasetID(2, datasetID)

    withResults {
      if (next())
        DatasetRevisionRecord(
          revisionID   = reqDatasetID("revision_id"),
          action       = DatasetRevision.Action.fromID(getInt("action").toUByte()),
          timestamp    = get("timestamp"),
          revisionNote = "",
          originalID   = reqDatasetID("original_id"),
        )
      else
        null
    }
  }

