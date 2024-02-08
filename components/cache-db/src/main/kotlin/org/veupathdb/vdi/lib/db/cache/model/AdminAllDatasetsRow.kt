package org.veupathdb.vdi.lib.db.cache.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

data class AdminAllDatasetsRow(
  val datasetID: DatasetID,
  val ownerID: UserID,
  val origin: String,
  val created: OffsetDateTime,
  val typeName: String,
  val typeVersion: String,
  val isDeleted: Boolean,
  val name: String,
  val summary: String?,
  val description: String?,
  val sourceURL: String?,
  val visibility: VDIDatasetVisibility,
  val projectIDs: List<ProjectID>,
  val importStatus: DatasetImportStatus,
  val importMessage: String?
)
