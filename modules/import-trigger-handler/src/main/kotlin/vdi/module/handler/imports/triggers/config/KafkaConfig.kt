package vdi.module.handler.imports.triggers.config

import vdi.components.kafka.KafkaConsumerConfig

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
  val importTriggerMessageKey: String = KafkaConfigDefaults.ImportTriggerMessageKey,

  /**
   * Import Triggers Topic
   *
   * Name of the Kafka topic that import trigger messages will be listened for
   * on.
   */
  val importTriggerTopic: String = KafkaConfigDefaults.ImportTriggerTopic,

  /**
   * Import Results Message Key
   *
   * This value defines the message key that will be sent on import result
   * messages published to the configured Kafka topic: [importResultTopic].
   */
  val importResultMessageKey: String = KafkaConfigDefaults.ImportResultMessageKey,

  /**
   * Import Results Topic
   *
   * Name of the Kafka topic that import result messages will be published to.
   */
  val importResultTopic: String = KafkaConfigDefaults.ImportResultTopic,
)