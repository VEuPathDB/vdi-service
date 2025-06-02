package vdi.lib.install.cleanup

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

data class ReinstallTarget(
  val datasetID: DatasetID,
  val installTarget: InstallTargetID,
)
