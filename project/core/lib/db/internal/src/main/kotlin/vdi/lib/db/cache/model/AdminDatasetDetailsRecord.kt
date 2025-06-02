package vdi.lib.db.cache.model

import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import vdi.model.data.DatasetVisibility
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
  override val summary: String?,
  override val description: String?,
  override val sourceURL: String?,
  override val visibility: DatasetVisibility,
  override val projects: List<InstallTargetID>,
  override val importStatus: DatasetImportStatus,
  override val originalID: DatasetID?,
  val syncControl: SyncControlRecord?,
  val messages: List<String>,
  val installFiles: List<String>,
  val uploadFiles: List<String>
) : Dataset, CoreDatasetMeta, DatasetProjectLinks
