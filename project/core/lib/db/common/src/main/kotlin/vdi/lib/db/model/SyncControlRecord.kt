package vdi.lib.db.model

import vdi.model.data.DatasetID
import java.time.OffsetDateTime


open class SyncControlRecord(
  val datasetID: DatasetID,
  val sharesUpdated: OffsetDateTime,
  val dataUpdated: OffsetDateTime,
  val metaUpdated: OffsetDateTime,
)
