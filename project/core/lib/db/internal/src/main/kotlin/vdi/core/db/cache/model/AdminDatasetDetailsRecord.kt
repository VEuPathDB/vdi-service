package vdi.core.db.cache.model

import java.time.OffsetDateTime
import vdi.core.db.model.SyncControlRecord
import vdi.model.DatasetUploadStatus
import vdi.model.meta.*

data class AdminDatasetDetailsRecord(
  override val datasetID: DatasetID,
  override val ownerID: UserID,
  override val origin: String,
  override val created: OffsetDateTime,
  override val inserted: OffsetDateTime,
  override val type: DatasetType,
  override val isDeleted: Boolean,
  override val name: String,
  override val projectName: String?,
  override val programName: String?,
  override val summary: String,
  override val description: String?,
  override val visibility: DatasetVisibility,
  override val projects: List<InstallTargetID>,
  override val importStatus: DatasetImportStatus?,
  override val originalID: DatasetID?,
  override val uploadStatus: DatasetUploadStatus,
  val syncControl: SyncControlRecord?,
  val messages: List<String>,
  val installFiles: List<String>,
  val uploadFiles: List<String>
) : Dataset, CoreDatasetMeta, DatasetProjectLinks
