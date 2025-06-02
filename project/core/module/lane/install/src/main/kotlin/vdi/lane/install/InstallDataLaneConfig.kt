package vdi.lane.install

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

data class InstallDataLaneConfig(
  val workerPoolSize: UByte,
  val jobQueueSize:   UByte,
  val consumerConfig: KafkaConsumerConfig,
  val s3Config:       S3Config,
  val s3Bucket:       BucketName,
  val eventChannel:   MessageTopic,
  val eventMsgKey:    MessageKey,
) {
  constructor(conf: VDIConfig): this(conf.lanes?.install, conf.objectStore, conf.kafka)

  constructor(lane: ConsumerLaneConfig?, store: ObjectStoreConfig, kafka: KafkaConfig): this(
    workerPoolSize = lane?.workerCount ?: WorkerCount,
    jobQueueSize   = lane?.inMemoryQueueSize ?: JobQueueSize,
    consumerConfig = KafkaConsumerConfig(lane?.kafkaConsumerID ?: ConsumerID, kafka),
    s3Config       = S3Config(store),
    s3Bucket       = store.bucket,
    eventChannel   = lane?.eventChannel?.toMessageTopic() ?: EventChannel,
    eventMsgKey    = lane?.eventKey?.toMessageKey() ?: EventMsgKey,
  )

  private companion object {
    inline val WorkerCount
      get(): UByte = 5u

    inline val JobQueueSize
      get(): UByte = 5u

    inline val ConsumerID
      get() = "install-data-handler-receive"

    inline val EventChannel
      get() = RouterDefaults.InstallTriggerTopic

    inline val EventMsgKey
      get() = RouterDefaults.InstallTriggerMessageKey
  }
}
