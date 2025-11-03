package vdi.core.db.app.model

import vdi.model.data.DatasetID
import vdi.model.data.UserID

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
