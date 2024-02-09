package org.veupathdb.vdi.lib.kafka.router

import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaMessage
import org.veupathdb.vdi.lib.kafka.KafkaProducer
import org.veupathdb.vdi.lib.kafka.model.triggers.*


class KafkaRouter(
  private val config: KafkaRouterConfig,
  private val producer: KafkaProducer,
) {
  fun sendImportTrigger(trigger: ImportTrigger) {
    producer.send(
      config.importTriggerTopic,
      KafkaMessage(config.importTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendInstallTrigger(trigger: InstallTrigger) {
    producer.send(
      config.installTriggerTopic,
      KafkaMessage(config.installTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendUpdateMetaTrigger(trigger: UpdateMetaTrigger) {
    producer.send(
      config.updateMetaTriggerTopic,
      KafkaMessage(config.updateMetaTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendSoftDeleteTrigger(trigger: SoftDeleteTrigger) {
    producer.send(
      config.softDeleteTriggerTopic,
      KafkaMessage(config.softDeleteTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendHardDeleteTrigger(trigger: HardDeleteTrigger) {
    producer.send(
      config.hardDeleteTriggerTopic,
      KafkaMessage(config.hardDeleteTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendShareTrigger(trigger: ShareTrigger) {
    producer.send(
      config.shareTriggerTopic,
      KafkaMessage(config.shareTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun sendReconciliationTrigger(trigger: ReconciliationTrigger) {
    producer.send(
      config.reconciliationTriggerTopic,
      KafkaMessage(config.reconciliationTriggerMessageKey, JSON.writeValueAsString(trigger))
    )
  }

  fun close() {
    producer.close()
  }
}