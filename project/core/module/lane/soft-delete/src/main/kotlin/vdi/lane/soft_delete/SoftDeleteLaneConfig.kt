package vdi.lane.soft_delete

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.vdi.ObjectStoreConfig
import vdi.core.config.vdi.VDIConfig
import vdi.core.config.vdi.lanes.ConsumerLaneConfig
import vdi.core.kafka.*
import vdi.core.kafka.router.RouterDefaults
import vdi.core.s3.util.S3Config
import vdi.core.s3.util.bucket

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
