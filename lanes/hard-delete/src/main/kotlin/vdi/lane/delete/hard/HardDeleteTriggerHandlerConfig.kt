package vdi.lane.delete.hard

import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfigDefaults

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

    kafkaConsumerConfig = KafkaConsumerConfig(env.optional(EnvKey.HardDeleteTriggerHandler.KafkaConsumerClientID) ?: "hard-delete-handler", env),

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

