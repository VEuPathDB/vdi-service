package vdi.lane.delete.soft

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
import vdi.lib.env.EnvKey
import vdi.lib.env.Environment
import vdi.lib.kafka.KafkaConsumerConfig
import vdi.lib.kafka.router.RouterDefaults
import vdi.lib.s3.util.S3Config

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
      ?: RouterDefaults.SoftDeleteTriggerTopic,

    softDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
      ?: RouterDefaults.SoftDeleteTriggerMessageKey

  )

  object Defaults {
    const val WorkerPoolSize = 5u
    const val WorkQueueSize  = 5u
  }
}
