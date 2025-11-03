package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.model.data.*

data class AdminAllDatasetsRow(
  override val datasetID: DatasetID,
  override val ownerID: UserID,
  override val origin: String,
  override val created: OffsetDateTime,
  override val type: DatasetType,
  override val isDeleted: Boolean,
  override val name: String,
  override val shortName: String?,
  override val shortAttribution: String?,
  override val summary: String,
  override val description: String?,
  override val sourceURL: String?,
  override val visibility: DatasetVisibility,
  override val projects: List<InstallTargetID>,
  override val importStatus: DatasetImportStatus,
  override val inserted: OffsetDateTime,
  override val originalID: DatasetID?,
  val importMessage: String?,
  val uploadFiles: List<DatasetFileInfo>,
  val installFiles: List<DatasetFileInfo>,
) : Dataset, CoreDatasetMeta, DatasetProjectLinks
