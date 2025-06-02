package vdi.core.db.app.model

import java.time.OffsetDateTime
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.DatasetVisibility
import vdi.model.data.UserID

/**
 * Represents a single record in the `vdi.datasets` table.
 */
data class DatasetRecord(
  val datasetID: DatasetID,
  val owner: UserID,
  val type: DatasetType,
  val deletionState: DeleteFlag,
  val isPublic: Boolean,
  val accessibility: DatasetVisibility,
  val daysForApproval: Int,
  val creationDate: OffsetDateTime?,
)
