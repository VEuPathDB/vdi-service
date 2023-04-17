package vdi.module.handler.imports.triggers.config

import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults


data class KafkaConfig(
  /**
   * Kafka Consumer Client Configuration.
   */
  val consumerConfig: KafkaConsumerConfig,

  /**
   * Import Triggers Message Key
   *
   * This value is the expected message key for import trigger messages that
   * will be listened for coming from the import trigger topic.
   *
   * Messages received on the import trigger topic that do not have this message
   * key will be ignored with a warning.
   */
  val importTriggerMessageKey: String = KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,

  /**
   * Import Triggers Topic
   *
   * Name of the Kafka topic that import trigger messages will be listened for
   * on.
   */
  val importTriggerTopic: String = KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,

  )