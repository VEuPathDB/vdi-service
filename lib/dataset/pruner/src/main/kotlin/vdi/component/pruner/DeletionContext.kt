package vdi.component.pruner

import vdi.component.db.cache.model.DeletedDataset
import vdi.lib.logging.logger

internal data class DeletionContext(val dataset: DeletedDataset) {
  val logger = logger<Pruner>(datasetID, ownerID)

  lateinit var state: PrunableState

  inline val datasetID
    get() = dataset.datasetID

  inline val ownerID
    get() = dataset.ownerID

  inline val projects
    get() = dataset.projects

  inline val dataType
    get() = dataset.dataType
}
