package vdi.core.db.app.model

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

/**
 * Represents a single row in the `vdi.dataset_projects` table.
 *
 * The `vdi.dataset_projects` table declares which projects a dataset was
 * installed into in a single application database.  This is required due to the
 * fact that some application databases are shared between multiple projects.
 */
data class DatasetProjectLinkRecord(

  /**
   * ID of the dataset this record is for.
   */
  val datasetID: DatasetID,

  /**
   * ID of the target project this record links to.
   */
  val installTarget: InstallTargetID,
)
