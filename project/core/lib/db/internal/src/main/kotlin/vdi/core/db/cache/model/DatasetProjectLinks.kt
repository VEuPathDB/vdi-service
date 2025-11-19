package vdi.core.db.cache.model

import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID

interface DatasetProjectLinks {
  val datasetID: DatasetID
  val projects: List<InstallTargetID>
}
