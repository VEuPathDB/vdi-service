package vdi.component.kafka.router

import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optional
import vdi.component.env.EnvKey
import vdi.component.kafka.KafkaProducerConfig

data class TriggerConfig(val messageKey: String, val topic: String)

data class KafkaRouterConfig(
  val producerConfig: KafkaProducerConfig,

  val importTrigger: TriggerConfig,
  val installTrigger: TriggerConfig,
  val updateMetaTrigger: TriggerConfig,
  val softDeleteTrigger: TriggerConfig,
  val hardDeleteTrigger: TriggerConfig,
  val shareTrigger: TriggerConfig,
  val reconciliationTrigger: TriggerConfig,
  val revisionPruningTrigger: TriggerConfig,
) {
  constructor(env: Environment, clientID: String): this(
    producerConfig = KafkaProducerConfig(env, clientID),

    importTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
        ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
        ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,
    ),

    installTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
        ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
        ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC,
    ),

    updateMetaTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.UpdateMetaTriggers)
        ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.UpdateMetaTriggers)
        ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_TOPIC,
    ),

    softDeleteTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
        ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
        ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,
    ),

    hardDeleteTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
        ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
        ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,
    ),

    shareTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
        ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
        ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,
    ),

    reconciliationTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.ReconciliationTriggers)
        ?: KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ReconciliationTriggers)
        ?: KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_TOPIC,
    ),

    revisionPruningTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.RevisionPruningTriggers)
        ?: KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.RevisionPruningTriggers)
        ?: KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_TOPIC,
    ),
  )
}

