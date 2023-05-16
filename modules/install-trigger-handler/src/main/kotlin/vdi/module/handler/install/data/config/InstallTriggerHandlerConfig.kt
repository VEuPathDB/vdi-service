package vdi.module.handler.install.data.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

data class InstallTriggerHandlerConfig(
  val workerPoolSize:               UInt,
  val jobQueueSize:                 UInt,
  val kafkaConsumerConfig:          KafkaConsumerConfig,
  val s3Config:                     S3Config,
  val s3Bucket:                     BucketName,
  val installDataTriggerTopic:      String,
  val installDataTriggerMessageKey: String,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    workerPoolSize = env.optUInt(EnvKey.InstallDataTriggerHandler.WorkerPoolSize)
      ?: InstallTriggerHandlerConfigDefaults.WorkerPoolSize,

    jobQueueSize = env.optUInt(EnvKey.InstallDataTriggerHandler.WorkQueueSize)
      ?: InstallTriggerHandlerConfigDefaults.JobQueueSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.InstallDataTriggerHandler.KafkaConsumerClientID),
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    installDataTriggerTopic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
      ?: InstallTriggerHandlerConfigDefaults.InstallDataTriggerTopic,

    installDataTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
      ?: InstallTriggerHandlerConfigDefaults.InstallDataTriggerMessageKey,
  )
}

