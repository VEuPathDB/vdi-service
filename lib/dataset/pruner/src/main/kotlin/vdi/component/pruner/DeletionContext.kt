package vdi.component.pruner

import org.slf4j.Logger
import vdi.component.db.cache.model.DeletedDataset
import vdi.lib.logging.logger

internal class DeletionContext {
  lateinit var dataset: DeletedDataset

  lateinit var logger: Logger

  inline val datasetID
    get() = dataset.datasetID

  inline val ownerID
    get() = dataset.ownerID

  inline val projects
    get() = dataset.projects

  inline val dataType
    get() = dataset.dataType

  fun init(dataset: DeletedDataset) {
    this.dataset = dataset
    this.logger = logger<Pruner>(datasetID, ownerID)
  }
}
