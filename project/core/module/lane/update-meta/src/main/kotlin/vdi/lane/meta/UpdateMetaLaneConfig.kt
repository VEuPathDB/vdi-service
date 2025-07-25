package vdi.lane.meta

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.vdi.ObjectStoreConfig
import vdi.core.config.vdi.VDIConfig
import vdi.core.config.vdi.lanes.LaneConfig
import vdi.core.config.vdi.lanes.ProducerLaneConfig
import vdi.core.kafka.*
import vdi.core.kafka.router.KafkaRouterConfig
import vdi.core.kafka.router.RouterDefaults
import vdi.core.s3.util.S3Config
import vdi.core.s3.util.bucket

data class UpdateMetaLaneConfig(
  val workerCount:         UByte,
  val jobQueueSize:        UByte,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val kafkaRouterConfig:   KafkaRouterConfig,
  val s3Config:            S3Config,
  val s3Bucket:            BucketName,
  val eventKey:            MessageKey,
  val eventTopic:          MessageTopic,
) {
  constructor(config: VDIConfig): this(
    config.lanes?.updateMeta,
    config.objectStore,
    config.lanes,
    config.kafka,
  )

  constructor(
    lane:  ProducerLaneConfig?,
    store: ObjectStoreConfig,
    lanes: LaneConfig?,
    kafka: KafkaConfig,
  ): this(
    workerCount         = lane?.workerCount ?: WorkerCount,
    jobQueueSize        = lane?.inMemoryQueueSize ?: JobQueueSize,
    kafkaConsumerConfig = KafkaConsumerConfig(lane?.kafkaConsumerID ?: ConsumerID, kafka),
    kafkaRouterConfig   = KafkaRouterConfig(lane?.kafkaProducerID ?: ProducerID, kafka, lanes),
    s3Config            = S3Config(store),
    s3Bucket            = store.bucket,
    eventKey            = lane?.eventKey?.toMessageKey() ?: MessageKey,
    eventTopic          = lane?.eventChannel?.toMessageTopic() ?: MessageChannel,
  )

  private companion object {
    inline val WorkerCount
      get(): UByte = 10u
    inline val JobQueueSize
      get(): UByte = 5u
    inline val ConsumerID
      get() = "update-meta-receive"
    inline val ProducerID
      get() = "update-meta-send"
    inline val MessageKey
      get() = RouterDefaults.UpdateMetaTriggerMessageKey
    inline val MessageChannel
      get() = RouterDefaults.UpdateMetaTriggerTopic
  }
}
