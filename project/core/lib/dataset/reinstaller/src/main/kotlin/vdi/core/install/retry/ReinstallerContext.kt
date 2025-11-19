package vdi.core.install.retry

import org.slf4j.Logger
import vdi.core.db.app.model.DatasetRecord
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.meta.InstallTargetID

internal class ReinstallerContext(
  val eventID: EventID,
  val dataset: DatasetRecord,
  val installTarget: InstallTargetID,
  val manager: DatasetObjectStore,
  logger: Logger,
) {
  inline val datasetID get() = dataset.datasetID
  inline val owner get() = dataset.owner

  val logger = logger.mark(
    eventID = eventID,
    ownerID = owner,
    datasetID = datasetID,
    installTarget = installTarget,
  )
}