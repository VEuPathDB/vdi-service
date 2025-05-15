package vdi.lib.db.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.time.OffsetDateTime


open class SyncControlRecord(
  val datasetID: DatasetID,
  val sharesUpdated: OffsetDateTime,
  val dataUpdated: OffsetDateTime,
  val metaUpdated: OffsetDateTime,
)
