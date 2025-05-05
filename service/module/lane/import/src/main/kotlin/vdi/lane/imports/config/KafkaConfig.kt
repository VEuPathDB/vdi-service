package vdi.lane.imports.config

import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optional
import vdi.lib.env.EnvKey
import vdi.lib.kafka.KafkaConsumerConfig
import vdi.lib.kafka.router.RouterDefaults


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
  val importTriggerMessageKey: String = RouterDefaults.ImportTriggerMessageKey,

  /**
   * Import Triggers Topic
   *
   * Name of the Kafka topic that import trigger messages will be listened for
   * on.
   */
  val importTriggerTopic: String = RouterDefaults.ImportTriggerTopic,
) {
  constructor(clientID: String) : this(System.getenv(), clientID)

  constructor(env: Environment, clientID: String) : this(
    consumerConfig          = KafkaConsumerConfig(clientID, env),
    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
      ?: RouterDefaults.ImportTriggerMessageKey,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
      ?: RouterDefaults.ImportTriggerTopic,
  )
}
