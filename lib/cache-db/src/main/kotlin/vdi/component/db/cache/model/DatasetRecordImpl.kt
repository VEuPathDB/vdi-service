package vdi.component.db.cache.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

data class DatasetRecordImpl(
  override val datasetID: DatasetID,
  override val typeName: DataType,
  override val typeVersion: String,
  override val ownerID: UserID,
  override val isDeleted: Boolean,
  override val created: OffsetDateTime,
  override val importStatus: DatasetImportStatus,
  override val origin: String,
  override val visibility: VDIDatasetVisibility,
  override val name: String,
  override val shortName: String?,
  override val shortAttribution: String?,
  override val category: String?,
  override val summary: String?,
  override val description: String?,
  override val sourceURL: String?,
  override val projects: Collection<String>,
  override val inserted: OffsetDateTime,
) : DatasetRecord
