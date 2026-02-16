package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import java.time.OffsetDateTime
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetType

data class DatasetImpl(
  override val datasetID: DatasetID,
  override val type: DatasetType,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus?,
  override val origin: String,
  override val inserted: OffsetDateTime,
  override val uploadStatus: DatasetUploadStatus,
): Dataset
