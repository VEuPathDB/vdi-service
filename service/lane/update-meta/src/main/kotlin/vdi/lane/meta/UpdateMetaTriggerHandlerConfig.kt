package vdi.lane.meta

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.component.s3.util.S3Config

data class UpdateMetaTriggerHandlerConfig(
  val workerPoolSize: UInt,
  val workQueueSize: UInt,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val kafkaRouterConfig: KafkaRouterConfig,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    workerPoolSize = env.optUInt(EnvKey.UpdateMetaTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    workQueueSize = env.optUInt(EnvKey.UpdateMetaTriggerHandler.WorkQueueSize)
      ?: Defaults.WorkQueueSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.optional(EnvKey.UpdateMetaTriggerHandler.KafkaConsumerClientID) ?: "update-meta-handler",
      env
    ),

    kafkaRouterConfig = KafkaRouterConfig(env, "update-meta"),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),
  )

  object Defaults {
    const val WorkerPoolSize = 10u
    const val WorkQueueSize = 5u
  }
}
