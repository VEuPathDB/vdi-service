package vdi.lane.sharing

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.meta.InstallTargetID

class ShareContext(
  private val msg: EventMessage,
  val store: DatasetObjectStore,
  logger: Logger,
) {
  private val realLogger = logger.mark(msg.eventID, msg.userID, msg.datasetID)
  var logger = realLogger
    private set

  val eventID
    get() = msg.eventID

  val ownerID
    get() = msg.userID

  val datasetID
    get() = msg.datasetID

  val eventSource
    get() = msg.eventSource

  fun loggingForInstallTarget(installTarget: InstallTargetID?) {
    logger = when (installTarget) {
      null -> realLogger
      else -> realLogger.mark(installTarget = installTarget)
    }
  }
}