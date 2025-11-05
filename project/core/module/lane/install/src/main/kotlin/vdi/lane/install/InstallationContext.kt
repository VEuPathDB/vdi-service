package vdi.lane.install

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.s3.DatasetObjectStore
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.UserID

class InstallationContext(
  val eventID: EventID,
  val userID: UserID,
  val datasetID: DatasetID,
  val store: DatasetObjectStore,
  val logger: Logger
) {
  constructor(
    msg:    EventMessage,
    store:  DatasetObjectStore,
    logger: Logger,
  ): this(
    eventID   = msg.eventID,
    userID    = msg.userID,
    datasetID = msg.datasetID,
    store     = store,
    logger    = logger.mark(msg.eventID, msg.userID, msg.datasetID)
  )
}