package vdi.lane.reconciliation

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.vdi.ObjectStoreConfig
import vdi.config.raw.vdi.VDIConfig
import vdi.config.raw.vdi.lanes.LaneConfig
import vdi.core.kafka.*
import vdi.core.kafka.router.KafkaRouterConfig
import vdi.core.kafka.router.RouterDefaults
import vdi.core.s3.util.S3Config

data class ReconciliationLaneConfig(
  val workerCount:    UByte,
  val jobQueueSize:   UByte,
  val kafkaInConfig:  KafkaConsumerConfig,
  val kafkaOutConfig: KafkaRouterConfig,
  val eventChannel:   MessageTopic,
  val eventMsgKey:    MessageKey,
  val s3Config:       S3Config,
  val s3Bucket:       BucketName,
) {
  constructor(conf: VDIConfig): this(conf.lanes, conf.objectStore, conf.kafka)

  constructor(lanes: LaneConfig?, obj: ObjectStoreConfig, kafka: KafkaConfig): this(
    workerCount    = lanes?.reconciliation?.inMemoryQueueSize ?: JobQueueSize,
    jobQueueSize   = lanes?.reconciliation?.workerCount ?: WorkerPoolSize,
    kafkaInConfig  = KafkaConsumerConfig(lanes?.reconciliation?.kafkaConsumerID ?: ConsumerID, kafka),
    kafkaOutConfig = KafkaRouterConfig(lanes?.reconciliation?.kafkaProducerID ?: ProducerID, kafka, lanes),
    eventChannel   = lanes?.reconciliation?.eventChannel?.toMessageTopic() ?: KafkaTopic,
    eventMsgKey    = lanes?.reconciliation?.eventKey?.toMessageKey() ?: KafkaMessageKey,
    s3Config       = S3Config(obj),
    s3Bucket       = BucketName(obj.bucketName),
  )

  private companion object {
    inline val ConsumerID
      get() = "reconciliation-lane-receive"

    inline val ProducerID
      get() = "reconciliation-lane-send"

    inline val KafkaTopic
      get() = RouterDefaults.ReconciliationTriggerTopic

    inline val KafkaMessageKey
      get() = RouterDefaults.ReconciliationTriggerMessageKey

    inline val JobQueueSize
      get(): UByte = 10u

    inline val WorkerPoolSize
      get(): UByte = 10u
  }
}

