package vdi.lib.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.time.OffsetDateTime
import vdi.model.data.DatasetType

data class DatasetImpl(
  override val datasetID: DatasetID,
  override val type: DatasetType,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus,
  override val origin: String,
  override val inserted: OffsetDateTime,
): Dataset
