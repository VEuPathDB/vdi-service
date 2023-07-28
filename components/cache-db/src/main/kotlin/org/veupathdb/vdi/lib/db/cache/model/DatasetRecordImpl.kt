package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

data class DatasetRecordImpl(
  override val datasetID: DatasetID,
  override val typeName: String,
  override val typeVersion: String,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus,
  override val origin: String,
  override val visibility: VDIDatasetVisibility,
  override val name: String,
  override val summary: String?,
  override val description: String?,
  override val files: Collection<String>,
  override val projects: Collection<String>
) : DatasetRecord
