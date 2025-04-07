package vdi.component.db.cache.sql.insert

import io.foxcapades.kdbc.setUByte
import io.foxcapades.kdbc.withPreparedBatchUpdate
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision
import vdi.component.db.jdbc.setDatasetID
import vdi.component.db.jdbc.setDateTime
import java.sql.Connection

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
  }.reduce(Int::plus)
