package vdi.core.db.app.model

import java.time.OffsetDateTime
import vdi.model.meta.*

/**
 * Represents a single record in the `vdi.datasets` table.
 */
data class DatasetRecord(
  val datasetID: DatasetID,
  val owner: UserID,
  val type: DatasetType,
  val category: String,
  val deletionState: DeleteFlag,
  val accessibility: DatasetVisibility,
  val daysForApproval: Int,
  val creationDate: OffsetDateTime,
) {
  constructor(
    datasetID:       DatasetID,
    meta:            DatasetMetadata,
    category:        String,
    deletionState:   DeleteFlag = DeleteFlag.NotDeleted,
    daysForApproval: Int = -1,
  ): this (
    datasetID,
    meta.owner,
    meta.type,
    category,
    deletionState,
    meta.visibility,
    daysForApproval,
    meta.created,
  )

  inline val isPublic get() = accessibility == DatasetVisibility.Public
}
