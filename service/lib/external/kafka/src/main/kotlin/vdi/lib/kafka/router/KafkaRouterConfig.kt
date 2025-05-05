package vdi.lib.kafka.router

import vdi.lib.config.vdi.KafkaConfig
import vdi.lib.config.vdi.lanes.LaneConfig
import vdi.lib.kafka.KafkaProducerConfig

data class KafkaRouterConfig(
  val producerConfig: KafkaProducerConfig,

  val importTrigger: TriggerConfig,
  val installTrigger: TriggerConfig,
  val updateMetaTrigger: TriggerConfig,
  val softDeleteTrigger: TriggerConfig,
  val hardDeleteTrigger: TriggerConfig,
  val shareTrigger: TriggerConfig,
  val reconciliationTrigger: TriggerConfig,
) {
  constructor(clientID: String, kafkaConfig: KafkaConfig, lanes: LaneConfig): this(
    producerConfig = KafkaProducerConfig(clientID, kafkaConfig),

    importTrigger = TriggerConfig(
      messageKey = lanes.import?.eventKey
        ?: RouterDefaults.ImportTriggerMessageKey,
      topic = lanes.import?.eventChannel
        ?: RouterDefaults.ImportTriggerTopic,
    ),

    installTrigger = TriggerConfig(
      messageKey = lanes.install?.eventKey
        ?: RouterDefaults.InstallTriggerMessageKey,
      topic = lanes.install?.eventChannel
        ?: RouterDefaults.InstallTriggerTopic,
    ),

    updateMetaTrigger = TriggerConfig(
      messageKey = lanes.updateMeta?.eventKey
        ?: RouterDefaults.UpdateMetaTriggerMessageKey,
      topic = lanes.updateMeta?.eventChannel
        ?: RouterDefaults.UpdateMetaTriggerTopic,
    ),

    softDeleteTrigger = TriggerConfig(
      messageKey = lanes.softDelete?.eventKey
        ?: RouterDefaults.SoftDeleteTriggerMessageKey,
      topic = lanes.softDelete?.eventChannel
        ?: RouterDefaults.SoftDeleteTriggerTopic,
    ),

    hardDeleteTrigger = TriggerConfig(
      messageKey = lanes.hardDelete?.eventKey
        ?: RouterDefaults.HardDeleteTriggerMessageKey,
      topic = lanes.hardDelete?.eventChannel
        ?: RouterDefaults.HardDeleteTriggerTopic,
    ),

    shareTrigger = TriggerConfig(
      messageKey = lanes.sharing?.eventKey
        ?: RouterDefaults.ShareTriggerMessageKey,
      topic = lanes.sharing?.eventChannel
        ?: RouterDefaults.ShareTriggerTopic,
    ),

    reconciliationTrigger = TriggerConfig(
      messageKey = lanes.reconciliation?.eventKey
        ?: RouterDefaults.ReconciliationTriggerMessageKey,
      topic = lanes.reconciliation?.eventChannel
        ?: RouterDefaults.ReconciliationTriggerTopic,
    ),
  )
}

