package vdi.component.db.cache.model

import java.time.OffsetDateTime
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

internal data class DatasetRecordImpl(
  override val datasetID: DatasetID,
  override val typeName: String,
  override val typeVersion: String,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus,
  override val name: String,
  override val summary: String?,
  override val description: String?,
  override val files: Collection<String>,
  override val projects: Collection<String>
) : DatasetRecord
