package vdi.components.kafka.router

import vdi.components.kafka.KafkaProducerConfig

data class KafkaRouterConfig(
  val producerConfig: KafkaProducerConfig,

  val importTriggerMessageKey: String = KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
  val importTriggerTopic: String = KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,

  val installTriggerMessageKey: String = KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
  val installTriggerTopic: String = KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC,

  val updateMetaTriggerMessageKey: String = KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
  val updateMetaTriggerTopic: String = KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_TOPIC,

  val softDeleteTriggerMessageKey: String = KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
  val softDeleteTriggerTopic: String = KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,

  val hardDeleteTriggerMessageKey: String = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
  val hardDeleteTriggerTopic: String = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,

  val shareTriggerMessageKey: String = KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
  val shareTriggerTopic: String = KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,
)

