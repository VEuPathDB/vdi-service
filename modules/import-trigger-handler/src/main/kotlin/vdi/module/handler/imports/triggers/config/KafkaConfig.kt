package vdi.module.handler.imports.triggers.config

import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
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
) {
  constructor(clientID: String) : this(System.getenv(), clientID)

  constructor(env: Environment, clientID: String) : this(
    consumerConfig          = KafkaConsumerConfig(clientID, env),
    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
      ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
      ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,
  )
}
