package vdi.module.delete.hard

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults

data class HardDeleteTriggerHandlerConfig(
  val jobQueueSize: UInt,
  val workerPoolSize: UInt,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val hardDeleteTopic: String,
  val hardDeleteMessageKey: String,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    jobQueueSize = env.optUInt(EnvKey.HardDeleteTriggerHandler.WorkQueueSize)
      ?: Defaults.JobQueueSize,

    workerPoolSize = env.optUInt(EnvKey.HardDeleteTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.HardDeleteTriggerHandler.KafkaConsumerClientID),
      env
    ),

    hardDeleteTopic = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
      ?: Defaults.HardDeleteTopic,

    hardDeleteMessageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
      ?: Defaults.HardDeleteMesesageKey
  )

  object Defaults {
    const val JobQueueSize   = 10u
    const val WorkerPoolSize = 10u

    inline val HardDeleteTopic
      get() = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC

    inline val HardDeleteMesesageKey
      get() = KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY
  }
}

