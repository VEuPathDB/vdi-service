package vdi.lib.kafka.router

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.json.JSON
import vdi.lib.kafka.EventMessage
import vdi.lib.kafka.EventSource
import vdi.lib.kafka.KafkaMessage
import vdi.lib.kafka.KafkaProducer

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
