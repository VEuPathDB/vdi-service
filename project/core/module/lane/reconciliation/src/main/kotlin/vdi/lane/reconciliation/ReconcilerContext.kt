package vdi.lane.reconciliation

import org.slf4j.Logger
import vdi.core.kafka.EventMessage
import vdi.core.kafka.EventSource
import vdi.logging.mark
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

internal class ReconcilerContext(
  val eventID: EventID,
  val datasetID: DatasetID,
  val ownerID: UserID,
  val source: EventSource,
  logger: Logger,
) {
  val logger = logger.mark(eventID, ownerID, datasetID)

  constructor(event: EventMessage, logger: Logger): this(
    event.eventID,
    event.datasetID,
    event.userID,
    event.eventSource,
    logger,
  )
}