package vdi.lane.hard_delete

import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.vdi.VDIConfig
import vdi.config.raw.vdi.lanes.ConsumerLaneConfig
import vdi.core.kafka.*
import vdi.core.kafka.router.RouterDefaults

data class HardDeleteLaneConfig(
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

