package vdi.component.kafka.router

import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optional
import vdi.component.env.EnvKey
import vdi.component.kafka.EventSource
import vdi.component.kafka.KafkaProducerConfig

data class KafkaRouterConfig(
  val producerConfig: KafkaProducerConfig,

  val eventSource: EventSource,

  val importTriggerMessageKey: String,
  val importTriggerTopic: String,

  val installTriggerMessageKey: String,
  val installTriggerTopic: String,

  val updateMetaTriggerMessageKey: String,
  val updateMetaTriggerTopic: String,

  val softDeleteTriggerMessageKey: String,
  val softDeleteTriggerTopic: String,

  val hardDeleteTriggerMessageKey: String,
  val hardDeleteTriggerTopic: String,

  val shareTriggerMessageKey: String,
  val shareTriggerTopic: String,

  val reconciliationTriggerMessageKey: String,
  val reconciliationTriggerTopic: String,
) {
  constructor(env: Environment, clientID: String, source: EventSource) : this(
    producerConfig = KafkaProducerConfig(env, clientID),

    eventSource = source,

    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
      ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
    importTriggerTopic = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
      ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,

    installTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
      ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
    installTriggerTopic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
      ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC,

    updateMetaTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.UpdateMetaTriggers)
      ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
    updateMetaTriggerTopic = env.optional(EnvKey.Kafka.Topic.UpdateMetaTriggers)
      ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_TOPIC,

    softDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
    softDeleteTriggerTopic = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,

    hardDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
      ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
    hardDeleteTriggerTopic = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
      ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,

    shareTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
    shareTriggerTopic = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,

    reconciliationTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ReconciliationTriggers)
      ?: KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_MESSAGE_KEY,
    reconciliationTriggerTopic = env.optional(EnvKey.Kafka.Topic.ReconciliationTriggers)
      ?: KafkaRouterConfigDefaults.RECONCILIATION_TRIGGER_TOPIC,
  )
}

