package vdi.core.kafka.router

import kotlinx.coroutines.runBlocking
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.json.JSON
import vdi.core.kafka.EventMessage
import vdi.core.kafka.EventSource
import vdi.core.kafka.KafkaMessage
import vdi.core.kafka.KafkaProducer
import vdi.logging.createLoggerMark
import vdi.logging.logger
import vdi.model.EventID
import vdi.util.events.EventIDs

class KafkaRouter(
  private val config: KafkaRouterConfig,
  private val producer: KafkaProducer,
) {
  private val logger = logger()

  fun sendImportTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.importTrigger, userID, datasetID, source)

  fun sendImportTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.importTrigger, eventID, userID, datasetID, source)

  fun sendInstallTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.installTrigger, userID, datasetID, source)

  fun sendInstallTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.installTrigger, eventID, userID, datasetID, source)

  fun sendUpdateMetaTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.updateMetaTrigger, userID, datasetID, source)

  fun sendUpdateMetaTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.updateMetaTrigger, eventID, userID, datasetID, source)

  fun sendSoftDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.softDeleteTrigger, userID, datasetID, source)

  fun sendSoftDeleteTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.softDeleteTrigger, eventID, userID, datasetID, source)

  fun sendHardDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.hardDeleteTrigger, userID, datasetID, source)

  fun sendHardDeleteTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.hardDeleteTrigger, eventID, userID, datasetID, source)

  fun sendShareTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.shareTrigger, userID, datasetID, source)

  fun sendShareTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.shareTrigger, eventID, userID, datasetID, source)

  fun sendReconciliationTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.reconciliationTrigger, userID, datasetID, source)

  fun sendReconciliationTrigger(eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.reconciliationTrigger, eventID, userID, datasetID, source)

  fun close() = producer.close()

  private fun send(tc: TriggerConfig, userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(tc, runBlocking { EventIDs.issueID() }, userID, datasetID, source)

  private fun send(tc: TriggerConfig, eventID: EventID, userID: UserID, datasetID: DatasetID, source: EventSource) {
    logger.debug("{} sending message {} from {}", createLoggerMark(
      eventID = eventID,
      ownerID = userID,
      datasetID = datasetID,
    ), tc, source)

    producer.send(
      tc.topic,
      KafkaMessage(tc.messageKey, JSON.writeValueAsString(EventMessage(
        eventID,
        userID,
        datasetID,
        source,
      )))
    )
  }
}
