package vdi.lane.soft_delete

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.logging.mark

class SoftDeleteContext(
  private val msg: EventMessage,
  logger: Logger,
) {
  val eventID
    get() = msg.eventID

  val ownerID
    get() = msg.userID

  val datasetID
    get() = msg.datasetID

  val eventSource
    get() = msg.eventSource

  val logger = logger.mark(msg.eventID, msg.userID, msg.datasetID)
}