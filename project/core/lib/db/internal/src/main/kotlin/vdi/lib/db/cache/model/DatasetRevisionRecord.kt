package vdi.lib.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.DatasetRevision
import java.time.OffsetDateTime

class DatasetRevisionRecord(
  action: Action,
  revisionID: DatasetID,
  revisionNote: String,
  timestamp: OffsetDateTime,
  val originalID: DatasetID,
): DatasetRevision(action, timestamp, revisionID, revisionNote)
