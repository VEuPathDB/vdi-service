package vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime
import vdi.lib.db.model.SyncControlRecord

data class AdminDatasetDetailsRecord(
  override val datasetID: DatasetID,
  override val ownerID: UserID,
  override val origin: String,
  override val created: OffsetDateTime,
  override val inserted: OffsetDateTime,
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
  override val originalID: DatasetID?,
  val syncControl: SyncControlRecord?,
  val messages: List<String>,
  val installFiles: List<String>,
  val uploadFiles: List<String>
) : Dataset, DatasetMeta, DatasetProjectLinks
