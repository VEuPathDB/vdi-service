package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

data class AdminAllDatasetsRow(
  override val datasetID: DatasetID,
  override val ownerID: UserID,
  override val origin: String,
  override val created: OffsetDateTime,
  override val typeName: String,
  override val typeVersion: String,
  override val isDeleted: Boolean,
  override val name: String,
  override val summary: String?,
  override val description: String?,
  override val sourceURL: String?,
  override val visibility: VDIDatasetVisibility,
  val projectIDs: List<ProjectID>,
  override val importStatus: DatasetImportStatus,
  val importMessage: String?,
  override val inserted: OffsetDateTime,
) : Dataset, DatasetMeta
