package vdi.lib.kafka.router

import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optional
import vdi.lib.env.EnvKey
import vdi.lib.kafka.KafkaProducerConfig

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
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,
    ),

    installTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC,
    ),

    updateMetaTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.UpdateMetaTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.UpdateMetaTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_TOPIC,
    ),

    softDeleteTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,
    ),

    hardDeleteTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,
    ),

    shareTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,
    ),

    reconciliationTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.ReconciliationTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.ReconciliationTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_TOPIC,
    ),

    revisionPruningTrigger = TriggerConfig(
      messageKey = env.optional(EnvKey.Kafka.MessageKey.RevisionPruningTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_MESSAGE_KEY,
      topic = env.optional(EnvKey.Kafka.Topic.RevisionPruningTriggers)
        ?: vdi.lib.kafka.router.KafkaRouterConfigDefaults.REVISION_PRUNE_TRIGGER_TOPIC,
    ),
  )
}

