package vdi.lane.sharing

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark

class ShareContext(
  private val msg: EventMessage,
  val store: DatasetObjectStore,
  logger: Logger,
) {
  val logger = logger.mark(msg.eventID, msg.userID, msg.datasetID)

  val eventID
    get() = msg.eventID

  val ownerID
    get() = msg.userID

  val datasetID
    get() = msg.datasetID

  val eventSource
    get() = msg.eventSource
}