package vdi.lib.db.app.model

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import java.time.OffsetDateTime

/**
 * Represents a single record in the `vdi.datasets` table.
 */
data class DatasetRecord(

  /**
   * ID of the dataset.
   */
  val datasetID: DatasetID,

  /**
   * ID of the owning user of the dataset.
   */
  val owner: UserID,

  /**
   * Name of the dataset type.
   */
  val typeName: DataType,

  /**
   * Version of the dataset type.
   */
  val typeVersion: String,

  /**
   * Whether the dataset has been marked as deleted.
   */
  val deletionState: DeleteFlag,

  /**
   * Indicates whether the dataset is marked as public.
   */
  val isPublic: Boolean,

  val accessibility: VDIDatasetVisibility,
  val daysForApproval: Int,
  val creationDate: OffsetDateTime?,
)
