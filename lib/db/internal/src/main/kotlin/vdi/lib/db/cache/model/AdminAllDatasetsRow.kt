package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetFileInfo
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
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
  override val category: String?,
  override val summary: String?,
  override val description: String?,
  override val sourceURL: String?,
  override val visibility: VDIDatasetVisibility,
  override val projects: List<ProjectID>,
  override val importStatus: DatasetImportStatus,
  override val inserted: OffsetDateTime,
  override val originalID: DatasetID?,
  val importMessage: String?,
  val uploadFiles: List<VDIDatasetFileInfo>,
  val installFiles: List<VDIDatasetFileInfo>,
) : Dataset, DatasetMeta, DatasetProjectLinks
