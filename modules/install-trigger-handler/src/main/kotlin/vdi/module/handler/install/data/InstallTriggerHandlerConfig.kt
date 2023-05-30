package vdi.module.handler.install.data

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults
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
      ?: Defaults.WorkerPoolSize,

    jobQueueSize = env.optUInt(EnvKey.InstallDataTriggerHandler.WorkQueueSize)
      ?: Defaults.JobQueueSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.InstallDataTriggerHandler.KafkaConsumerClientID),
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    installDataTriggerTopic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
      ?: Defaults.InstallDataTriggerTopic,

    installDataTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
      ?: Defaults.InstallDataTriggerMessageKey,
  )

  object Defaults {
    const val WorkerPoolSize = 5u

    const val JobQueueSize   = 5u

    inline val InstallDataTriggerTopic
      get() = KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC

    inline val InstallDataTriggerMessageKey
      get() = KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY
  }
}
