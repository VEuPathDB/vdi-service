package vdi.lib.db.cache.model

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import vdi.model.data.DatasetFileInfo
import vdi.model.data.DatasetVisibility
import java.time.OffsetDateTime

data class AdminAllDatasetsRow(
  override val datasetID: DatasetID,
  override val ownerID: UserID,
  override val origin: String,
  override val created: OffsetDateTime,
  override val typeName: DataType,
  override val typeVersion: String,
  override val isDeleted: Boolean,
  override val name: String,
  override val shortName: String?,
  override val shortAttribution: String?,
  override val summary: String?,
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
