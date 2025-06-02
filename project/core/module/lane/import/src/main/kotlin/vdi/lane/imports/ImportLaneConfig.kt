package vdi.lane.imports

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.config.raw.vdi.VDIConfig
import vdi.config.raw.vdi.lanes.ConsumerLaneConfig
import vdi.core.kafka.*
import vdi.core.kafka.router.RouterDefaults
import vdi.core.s3.util.S3Config
import vdi.core.s3.util.bucket

data class ImportLaneConfig(
  val workerCount:  UByte,
  val jobQueueSize: UByte,

  /**
   * Kafka Consumer Client Configuration.
   */
  val kafkaConfig: KafkaConsumerConfig,

  /**
   * Import Triggers Message Key
   *
   * This value is the expected message key for import trigger messages that
   * will be listened for coming from the import trigger topic.
   *
   * Messages received on the import trigger topic that do not have this message
   * key will be ignored with a warning.
   */
  val eventMsgKey: MessageKey,

  /**
   * Import Triggers Topic
   *
   * Name of the Kafka topic that import trigger messages will be listened for
   * on.
   */
  val eventChannel: MessageTopic,

  val s3Config: S3Config,

  val s3Bucket: BucketName,
) {
  constructor(conf: VDIConfig): this(conf.lanes?.import, conf.objectStore, conf.kafka)

  constructor(lane: ConsumerLaneConfig?, store: ObjectStoreConfig, kafka: KafkaConfig): this(
    workerCount  = lane?.workerCount ?: WorkerPoolSize,
    jobQueueSize = lane?.inMemoryQueueSize ?: WorkQueueSize,
    kafkaConfig  = KafkaConsumerConfig(lane?.kafkaConsumerID ?: ConsumerID, kafka),
    eventMsgKey  = lane?.eventKey?.toMessageKey() ?: EventKey,
    eventChannel = lane?.eventChannel?.toMessageTopic() ?: EventChannel,
    s3Config     = S3Config(store),
    s3Bucket     = store.bucket,
  )

  private companion object {
    inline val WorkerPoolSize
      get(): UByte = 10u

    inline val WorkQueueSize
      get(): UByte = 10u

    inline val ConsumerID
      get() = "import-lane-receive"

    inline val EventKey
      get() = RouterDefaults.ImportTriggerMessageKey

    inline val EventChannel
      get() = RouterDefaults.ImportTriggerTopic
  }
}
