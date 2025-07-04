package vdi.core.kafka.router

import vdi.core.config.StackConfig
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.config.vdi.lanes.LaneConfig
import vdi.core.kafka.KafkaProducerConfig
import vdi.core.kafka.toMessageKey
import vdi.core.kafka.toMessageTopic

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
  constructor(clientID: String): this(clientID, loadAndCacheStackConfig())

  constructor(clientID: String, conf: StackConfig): this(
    clientID,
    conf.vdi.kafka,
    conf.vdi.lanes,
  )

  constructor(clientID: String, kafkaConfig: KafkaConfig, lanes: LaneConfig?): this(
    producerConfig = KafkaProducerConfig(clientID, kafkaConfig),

    importTrigger = TriggerConfig(
      messageKey = lanes?.import?.eventKey?.toMessageKey()
        ?: RouterDefaults.ImportTriggerMessageKey,
      topic = lanes?.import?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.ImportTriggerTopic,
    ),

    installTrigger = TriggerConfig(
      messageKey = lanes?.install?.eventKey?.toMessageKey()
        ?: RouterDefaults.InstallTriggerMessageKey,
      topic = lanes?.install?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.InstallTriggerTopic,
    ),

    updateMetaTrigger = TriggerConfig(
      messageKey = lanes?.updateMeta?.eventKey?.toMessageKey()
        ?: RouterDefaults.UpdateMetaTriggerMessageKey,
      topic = lanes?.updateMeta?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.UpdateMetaTriggerTopic,
    ),

    softDeleteTrigger = TriggerConfig(
      messageKey = lanes?.softDelete?.eventKey?.toMessageKey()
        ?: RouterDefaults.SoftDeleteTriggerMessageKey,
      topic = lanes?.softDelete?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.SoftDeleteTriggerTopic,
    ),

    hardDeleteTrigger = TriggerConfig(
      messageKey = lanes?.hardDelete?.eventKey?.toMessageKey()
        ?: RouterDefaults.HardDeleteTriggerMessageKey,
      topic = lanes?.hardDelete?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.HardDeleteTriggerTopic,
    ),

    shareTrigger = TriggerConfig(
      messageKey = lanes?.sharing?.eventKey?.toMessageKey()
        ?: RouterDefaults.ShareTriggerMessageKey,
      topic = lanes?.sharing?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.ShareTriggerTopic,
    ),

    reconciliationTrigger = TriggerConfig(
      messageKey = lanes?.reconciliation?.eventKey?.toMessageKey()
        ?: RouterDefaults.ReconciliationTriggerMessageKey,
      topic = lanes?.reconciliation?.eventChannel?.toMessageTopic()
        ?: RouterDefaults.ReconciliationTriggerTopic,
    ),
  )
}

