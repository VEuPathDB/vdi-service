package org.veupathdb.vdi.lib.kafka.router

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.EventMessage
import org.veupathdb.vdi.lib.kafka.KafkaMessage
import org.veupathdb.vdi.lib.kafka.KafkaProducer

class KafkaRouter(
  private val config: KafkaRouterConfig,
  private val producer: KafkaProducer,
) {
  fun sendImportTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.importTriggerTopic, config.importTriggerMessageKey, userID, datasetID)

  fun sendInstallTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.installTriggerTopic, config.installTriggerMessageKey, userID, datasetID)

  fun sendUpdateMetaTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.updateMetaTriggerTopic, config.updateMetaTriggerMessageKey, userID, datasetID)

  fun sendSoftDeleteTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.softDeleteTriggerTopic, config.softDeleteTriggerMessageKey, userID, datasetID)

  fun sendHardDeleteTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.hardDeleteTriggerTopic, config.hardDeleteTriggerMessageKey, userID, datasetID)

  fun sendShareTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.shareTriggerTopic, config.shareTriggerMessageKey, userID, datasetID)

  fun sendReconciliationTrigger(userID: UserID, datasetID: DatasetID) =
    send(config.reconciliationTriggerTopic, config.reconciliationTriggerMessageKey, userID, datasetID)

  fun close() = producer.close()

  private fun send(topic: String, key: String, userID: UserID, datasetID: DatasetID) =
    producer.send(topic, KafkaMessage(key, JSON.writeValueAsString(EventMessage(userID, datasetID, config.eventSource))))
}