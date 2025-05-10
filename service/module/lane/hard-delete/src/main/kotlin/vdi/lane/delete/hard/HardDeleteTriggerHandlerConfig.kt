package vdi.lane.delete.hard

import vdi.lib.config.vdi.KafkaConfig
import vdi.lib.config.vdi.VDIConfig
import vdi.lib.config.vdi.lanes.ConsumerLaneConfig
import vdi.lib.kafka.*
import vdi.lib.kafka.router.RouterDefaults

data class HardDeleteTriggerHandlerConfig(
  val jobQueueSize:   UByte,
  val workerPoolSize: UByte,
  val kafkaConfig:    KafkaConsumerConfig,
  val eventChannel:   MessageTopic,
  val eventMsgKey:    MessageKey,
) {
  constructor(config: VDIConfig): this(config.lanes?.hardDelete, config.kafka)

  constructor(lConf: ConsumerLaneConfig?, kConf: KafkaConfig): this(
    jobQueueSize   = lConf?.inMemoryQueueSize ?: Defaults.JobQueueSize,
    workerPoolSize = lConf?.workerCount ?: Defaults.WorkerPoolSize,
    kafkaConfig    = KafkaConsumerConfig(lConf?.kafkaConsumerID ?: Defaults.KafkaConsumerClientID, kConf),
    eventChannel   = lConf?.eventChannel?.toMessageTopic() ?: Defaults.HardDeleteTopic,
    eventMsgKey    = lConf?.eventKey?.toMessageKey() ?: Defaults.HardDeleteMessageKey,
  )

  object Defaults {
    inline val JobQueueSize
      get(): UByte = 10u

    inline val WorkerPoolSize
      get(): UByte = 10u

    inline val KafkaConsumerClientID
      get() = "hard-delete-lane-receive"

    inline val HardDeleteTopic
      get() = RouterDefaults.HardDeleteTriggerTopic

    inline val HardDeleteMessageKey
      get() = RouterDefaults.HardDeleteTriggerMessageKey
  }
}

