package vdi.lib.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.time.OffsetDateTime
import vdi.model.data.DatasetType

interface Dataset {
  val datasetID: DatasetID
  val type: DatasetType
  val ownerID: UserID
  val isDeleted: Boolean
  val created: OffsetDateTime
  val importStatus: DatasetImportStatus
  val origin: String
  val inserted: OffsetDateTime
}
