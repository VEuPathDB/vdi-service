package vdi.core.db.model

import vdi.model.meta.DatasetID
import java.time.OffsetDateTime


open class SyncControlRecord(
  val datasetID: DatasetID,
  val sharesUpdated: OffsetDateTime,
  val dataUpdated: OffsetDateTime,
  val metaUpdated: OffsetDateTime,
)
