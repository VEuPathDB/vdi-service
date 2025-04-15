package vdi.component.db.app.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

/**
 * Represents a single row in the `vdi.dataset_visibility` table in an
 * application database.
 *
 * Records in this table declare that a target dataset should be visible to a
 * target user.  If no such record exists for a dataset and user combination,
 * the dataset should not be visible to the user.
 */
data class DatasetVisibilityRecord(

  /**
   * ID of the dataset this visibility record is for.
   */
  val datasetID: DatasetID,

  /**
   * ID of the user this visibility record is for.
   */
  val userID: UserID
)
