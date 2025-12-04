package vdi.core.db.model

import vdi.model.meta.DatasetID
import java.time.OffsetDateTime
import vdi.model.OriginTimestamp


open class SyncControlRecord(
  val datasetID:     DatasetID,
  val sharesUpdated: OffsetDateTime = OriginTimestamp,
  val dataUpdated:   OffsetDateTime = OriginTimestamp,
  val metaUpdated:   OffsetDateTime = OriginTimestamp,
)
