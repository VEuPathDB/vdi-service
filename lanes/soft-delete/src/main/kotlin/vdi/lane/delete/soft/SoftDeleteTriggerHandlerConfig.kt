package vdi.lane.delete.soft

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfigDefaults
import vdi.component.s3.util.S3Config

data class SoftDeleteTriggerHandlerConfig(
  val workerPoolSize: UInt,
  val workQueueSize: UInt,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
  val softDeleteTriggerTopic: String,
  val softDeleteTriggerMessageKey: String,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    workerPoolSize = env.optUInt(EnvKey.SoftDeleteTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    workQueueSize = env.optUInt(EnvKey.SoftDeleteTriggerHandler.WorkQueueSize)
      ?: Defaults.WorkQueueSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.optional(EnvKey.SoftDeleteTriggerHandler.KafkaConsumerClientID) ?: "soft-delete-handler",
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    softDeleteTriggerTopic = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,

    softDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY

  )

  object Defaults {
    const val WorkerPoolSize = 5u
    const val WorkQueueSize  = 5u
  }
}
