package vdi.component.db.app.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

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
  val typeName: String,

  /**
   * Version of the dataset type.
   */
  val typeVersion: String,

  /**
   * Whether the dataset has been marked as deleted.
   */
  val isDeleted: DeleteFlag,
)
