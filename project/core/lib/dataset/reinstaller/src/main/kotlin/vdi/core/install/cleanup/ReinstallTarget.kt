package vdi.core.install.cleanup

import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

data class ReinstallTarget(
  val datasetID: DatasetID,
  val installTarget: InstallTargetID,
)
