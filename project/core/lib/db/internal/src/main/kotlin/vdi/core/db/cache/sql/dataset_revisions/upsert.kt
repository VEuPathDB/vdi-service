package vdi.core.db.cache.sql.dataset_revisions

import io.foxcapades.kdbc.setUByte
import io.foxcapades.kdbc.withPreparedBatchUpdate
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision
import java.sql.Connection
import vdi.core.db.jdbc.setDatasetID
import vdi.core.db.jdbc.setDateTime

// language=postgresql
private const val SQL = """
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

internal fun Connection.tryInsertDatasetRevisions(originalID: DatasetID, revisions: Iterable<DatasetRevision>) =
  withPreparedBatchUpdate(SQL, revisions) {
    setDatasetID(1, it.revisionID)
    setDatasetID(2, originalID)
    setUByte(3, it.action.id)
    setDateTime(4, it.timestamp)
  }.reduceOrNull(Int::plus) ?: 0
