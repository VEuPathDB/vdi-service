package vdi.lane.install

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
