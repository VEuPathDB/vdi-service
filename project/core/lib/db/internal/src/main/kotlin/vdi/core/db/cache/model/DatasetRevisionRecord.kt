package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetRevision
import java.time.OffsetDateTime

class DatasetRevisionRecord(
  action: Action,
  revisionID: DatasetID,
  revisionNote: String,
  timestamp: OffsetDateTime,
  val originalID: DatasetID,
): DatasetRevision(action, timestamp, revisionID, revisionNote)
