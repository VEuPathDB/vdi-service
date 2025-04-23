package vdi.lib.install.cleanup

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID

data class ReinstallTarget(
  val datasetID: DatasetID,
  val projectID: ProjectID,
)
