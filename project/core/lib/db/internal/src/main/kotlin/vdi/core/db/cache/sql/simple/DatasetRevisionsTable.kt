package vdi.core.db.cache.sql.simple

import io.foxcapades.kdbc.*
import java.sql.Connection
import vdi.core.db.cache.model.DatasetRevisionRecord
import vdi.core.db.jdbc.reqDatasetID
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision
import vdi.model.meta.DatasetRevisionHistory

internal object DatasetRevisionsTable {

  // language=postgresql
  private const val DeleteSQL = """
DELETE FROM
  vdi.dataset_revisions
WHERE
  original_id = ?
"""

  /**
   * Deletes dataset revision records with the given original/root dataset ID.
   *
   * @return The number of records deleted.
   */
  context(con: Connection)
  internal fun delete(datasetID: DatasetID) =
    con.withPreparedUpdate(DeleteSQL) { setDatasetID(1, datasetID) }

  // language=postgresql
  private const val InsertSQL = """
INSERT INTO
  vdi.dataset_revisions (
    revision_id
  , original_id
  , action
  , timestamp
  )
VALUES
  (?, ?, ?, ?)
ON CONFLICT DO NOTHING
"""

  context(con: Connection)
  internal fun tryInsert(originalID: DatasetID, revision: DatasetRevision) =
    con.withPreparedUpdate(InsertSQL) {
      setDatasetID(1, revision.revisionID)
      setDatasetID(2, originalID)
      setUByte(3, revision.action.id)
      setDateTime(4, revision.timestamp)
    }

  context(con: Connection)
  internal fun tryInsert(originalID: DatasetID, revisions: Iterable<DatasetRevision>) =
    con.withPreparedBatchUpdate(InsertSQL, revisions) {
      setDatasetID(1, it.revisionID)
      setDatasetID(2, originalID)
      setUByte(3, it.action.id)
      setDateTime(4, it.timestamp)
    }.reduceOrNull(Int::plus) ?: 0

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
  private const val SelectHistorySQL = """
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

  context(con: Connection)
  fun selectRevisionHistory(datasetID: DatasetID) =
    con.withPreparedStatement(SelectHistorySQL) {
      setDatasetID(1, datasetID)
      setDatasetID(2, datasetID)

      withResults {
        var originalID: DatasetID? = null

        val records = map {
          if (originalID == null)
            originalID = reqDatasetID("original_id")

          DatasetRevision(
            revisionID = reqDatasetID("revision_id"),
            action     = DatasetRevision.Action.fromID(it.getInt("action").toUByte()),
            timestamp  = it["timestamp"],

            // revisionNote is not stored in the cache DB and must be looked up
            // from the meta json file later.
            revisionNote = "",
          )
        }

        originalID?.let { DatasetRevisionHistory(it, records) }
      }
    }

  // language=postgresql
  private const val SelectLatestSQLStart = """
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

  private const val SelectLatestSQLEndExcludeDeleted = """  INNER JOIN vdi.datasets AS d
    ON r.revision_id = d.dataset_id
WHERE
  d.is_deleted = false
ORDER BY timestamp DESC
LIMIT 1"""

  private const val SelectLatestSQLEndIncludeDeleted = "ORDER BY timestamp DESC\nLIMIT 1"

  context(con: Connection)
  fun selectLatestRevision(datasetID: DatasetID, includeDeleted: Boolean) =
    con.withPreparedStatement(
      SelectLatestSQLStart
      + if (includeDeleted) SelectLatestSQLEndIncludeDeleted
        else SelectLatestSQLEndExcludeDeleted
    ) {
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

  // language=postgresql
  private const val SelectOriginalIDSQL = """
SELECT
  original_id
FROM
  vdi.dataset_revisions
WHERE
  revision_id = ?
"""

  context(con: Connection)
  fun selectOriginalDatasetID(datasetID: DatasetID) =
    con.withPreparedStatement(SelectOriginalIDSQL) {
      setDatasetID(1, datasetID)

      withResults {
        if (next())
          reqDatasetID(1)
        else
          datasetID
      }
    }
}