package vdi.core.kafka.router

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.json.JSON
import vdi.core.kafka.EventMessage
import vdi.core.kafka.EventSource
import vdi.core.kafka.KafkaMessage
import vdi.core.kafka.KafkaProducer

class KafkaRouter(
  private val config: KafkaRouterConfig,
  private val producer: KafkaProducer,
) {
  fun sendImportTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.importTrigger, userID, datasetID, source)

  fun sendInstallTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.installTrigger, userID, datasetID, source)

  fun sendUpdateMetaTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.updateMetaTrigger, userID, datasetID, source)

  fun sendSoftDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.softDeleteTrigger, userID, datasetID, source)

  fun sendHardDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.hardDeleteTrigger, userID, datasetID, source)

  fun sendShareTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.shareTrigger, userID, datasetID, source)

  fun sendReconciliationTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.reconciliationTrigger, userID, datasetID, source)

  fun close() = producer.close()

  private fun send(tc: TriggerConfig, userID: UserID, datasetID: DatasetID, source: EventSource) =
    producer.send(tc.topic, KafkaMessage(tc.messageKey, JSON.writeValueAsString(EventMessage(userID, datasetID, source))))
}
