package vdi.module.events.routing.config

import vdi.components.kafka.KafkaProducerConfig

internal data class KafkaConfig(
  val producerConfig: KafkaProducerConfig,

  val importTriggerMessageKey: String = KafkaConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
  val importTriggerTopic: String = KafkaConfigDefaults.IMPORT_TRIGGER_TOPIC,

  val installTriggerMessageKey: String = KafkaConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
  val installTriggerTopic: String = KafkaConfigDefaults.INSTALL_TRIGGER_TOPIC,

  val updateMetaTriggerMessageKey: String = KafkaConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
  val updateMetaTriggerTopic: String = KafkaConfigDefaults.UPDATE_META_TRIGGER_TOPIC,

  val softDeleteTriggerMessageKey: String = KafkaConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
  val softDeleteTriggerTopic: String = KafkaConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,

  val hardDeleteTriggerMessageKey: String = KafkaConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
  val hardDeleteTriggerTopic: String = KafkaConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,

  val shareTriggerMessageKey: String = KafkaConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
  val shareTriggerTopic: String = KafkaConfigDefaults.SHARE_TRIGGER_TOPIC,
)

