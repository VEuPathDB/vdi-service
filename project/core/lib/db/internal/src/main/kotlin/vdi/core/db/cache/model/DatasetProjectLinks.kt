package vdi.core.db.cache.model

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: List<InstallTargetID>
}
