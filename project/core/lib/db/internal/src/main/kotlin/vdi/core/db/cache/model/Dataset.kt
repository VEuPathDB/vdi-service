package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.UserID

interface Dataset {
  val datasetID: DatasetID
  val type: DatasetType
  val ownerID: UserID
  val isDeleted: Boolean
  val created: OffsetDateTime
  val uploadStatus: DatasetUploadStatus
  val importStatus: DatasetImportStatus?
  val origin: String
  val inserted: OffsetDateTime
}
