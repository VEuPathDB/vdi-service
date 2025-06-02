package vdi.lane.soft_delete

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.config.raw.vdi.VDIConfig
import vdi.config.raw.vdi.lanes.ConsumerLaneConfig
import vdi.lib.kafka.*
import vdi.lib.kafka.router.RouterDefaults
import vdi.lib.s3.util.S3Config
import vdi.lib.s3.util.bucket

data class SoftDeleteLaneConfig(
  val workerCount:  UByte,
  val jobQueueSize: UByte,
  val kafkaConfig:  KafkaConsumerConfig,
  val s3Config:     S3Config,
  val s3Bucket:     BucketName,
  val eventChannel: MessageTopic,
  val eventKey:     MessageKey,
) {
  constructor(config: VDIConfig): this(config.lanes?.softDelete, config.objectStore, config.kafka)

  constructor(lane: ConsumerLaneConfig?, store: ObjectStoreConfig, kafka: KafkaConfig): this(
    workerCount  = lane?.workerCount ?: WorkerPoolSize,
    jobQueueSize = lane?.inMemoryQueueSize ?: WorkQueueSize,
    kafkaConfig  = KafkaConsumerConfig(lane?.kafkaConsumerID ?: ConsumerID, kafka),
    s3Config     = S3Config(store),
    s3Bucket     = store.bucket,
    eventChannel = lane?.eventChannel?.toMessageTopic() ?: EventChannel,
    eventKey     = lane?.eventKey?.toMessageKey() ?: EventKey,
  )

  private companion object {
    inline val ConsumerID
      get() = "soft-delete-lane-receive"

    inline val EventKey
      get() = RouterDefaults.SoftDeleteTriggerMessageKey

    inline val EventChannel
      get() = RouterDefaults.SoftDeleteTriggerTopic

    inline val WorkerPoolSize
      get(): UByte = 5u

    inline val WorkQueueSize
      get(): UByte = 5u
  }
}
