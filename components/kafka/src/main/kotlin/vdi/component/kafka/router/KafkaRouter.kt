package vdi.component.kafka.router

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.json.JSON
import vdi.component.kafka.EventMessage
import vdi.component.kafka.EventSource
import vdi.component.kafka.KafkaMessage
import vdi.component.kafka.KafkaProducer

class KafkaRouter(
  private val config: KafkaRouterConfig,
  private val producer: KafkaProducer,
) {
  fun sendImportTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.importTriggerTopic, config.importTriggerMessageKey, userID, datasetID, source)

  fun sendInstallTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.installTriggerTopic, config.installTriggerMessageKey, userID, datasetID, source)

  fun sendUpdateMetaTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.updateMetaTriggerTopic, config.updateMetaTriggerMessageKey, userID, datasetID, source)

  fun sendSoftDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.softDeleteTriggerTopic, config.softDeleteTriggerMessageKey, userID, datasetID, source)

  fun sendHardDeleteTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.hardDeleteTriggerTopic, config.hardDeleteTriggerMessageKey, userID, datasetID, source)

  fun sendShareTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.shareTriggerTopic, config.shareTriggerMessageKey, userID, datasetID, source)

  fun sendReconciliationTrigger(userID: UserID, datasetID: DatasetID, source: EventSource) =
    send(config.reconciliationTriggerTopic, config.reconciliationTriggerMessageKey, userID, datasetID, source)

  fun close() = producer.close()

  private fun send(topic: String, key: String, userID: UserID, datasetID: DatasetID, source: EventSource) =
    producer.send(topic, KafkaMessage(key, JSON.writeValueAsString(EventMessage(userID, datasetID, source))))
}