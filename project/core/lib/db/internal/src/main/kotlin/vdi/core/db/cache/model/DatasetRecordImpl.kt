package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.model.data.*

data class DatasetRecordImpl(
  override val datasetID: DatasetID,
  override val type: DatasetType,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus,
  override val origin: String,
  override val visibility: DatasetVisibility,
  override val name: String,
  override val shortName: String?,
  override val shortAttribution: String?,
  override val summary: String,
  override val description: String?,
  override val sourceURL: String?,
  override val projects: List<String>,
  override val inserted: OffsetDateTime,
  override val originalID: DatasetID?,
): DatasetRecord
