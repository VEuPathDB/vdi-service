package vdi.module.delete.hard

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig

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
      ?: HardDeleteTriggerHandlerConfigDefaults.JobQueueSize,

    workerPoolSize = env.optUInt(EnvKey.HardDeleteTriggerHandler.WorkerPoolSize)
      ?: HardDeleteTriggerHandlerConfigDefaults.WorkerPoolSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.HardDeleteTriggerHandler.KafkaConsumerClientID),
      env
    ),

    hardDeleteTopic = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
      ?: HardDeleteTriggerHandlerConfigDefaults.HardDeleteTopic,

    hardDeleteMessageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
      ?: HardDeleteTriggerHandlerConfigDefaults.HardDeleteMesesageKey
  )
}

