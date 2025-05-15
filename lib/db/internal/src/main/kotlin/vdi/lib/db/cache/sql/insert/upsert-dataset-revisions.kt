package vdi.lib.db.cache.sql.insert

import io.foxcapades.kdbc.setUByte
import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import java.sql.Connection
import vdi.lib.db.jdbc.setDatasetID
import vdi.lib.db.jdbc.setDateTime

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

internal fun Connection.tryInsertDatasetRevisions(originalID: DatasetID, revisions: Iterable<VDIDatasetRevision>) =
  withPreparedBatchUpdate(SQL, revisions) {
    setDatasetID(1, it.revisionID)
    setDatasetID(2, originalID)
    setUByte(3, it.action.id)
    setDateTime(4, it.timestamp)
  }.reduceOrNull(Int::plus) ?: 0
