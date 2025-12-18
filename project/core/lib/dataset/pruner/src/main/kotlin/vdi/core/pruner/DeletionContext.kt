package vdi.core.pruner

import vdi.core.db.cache.model.DeletedDataset
import vdi.logging.markedLogger

internal data class DeletionContext(val dataset: DeletedDataset) {
  val logger = markedLogger(ownerID = dataset.ownerID, datasetID = dataset.datasetID)

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
