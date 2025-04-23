package vdi.lane.install

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.optional
import org.veupathdb.vdi.lib.common.env.require
import vdi.lib.env.EnvKey
import vdi.lib.env.Environment
import vdi.component.kafka.KafkaConsumerConfig
import vdi.component.kafka.router.KafkaRouterConfig
import vdi.lib.kafka.router.KafkaRouterConfigDefaults
import vdi.lib.s3.util.S3Config

data class InstallTriggerHandlerConfig(
  val workerPoolSize:               UInt,
  val jobQueueSize:                 UInt,
  val kafkaConsumerConfig:          KafkaConsumerConfig,
  val kafkaRouterConfig:            KafkaRouterConfig,
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
      env.optional(EnvKey.InstallDataTriggerHandler.KafkaConsumerClientID)
        ?: Defaults.KafkaConsumerClientID,
      env
    ),

    kafkaRouterConfig = KafkaRouterConfig(
      env,
      env.optional(EnvKey.InstallDataTriggerHandler.KafkaProducerClientID)
        ?: Defaults.KafkaProducerClientID
    ),

    s3Config = vdi.lib.s3.util.S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    installDataTriggerTopic = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
      ?: Defaults.InstallDataTriggerTopic,

    installDataTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
      ?: Defaults.InstallDataTriggerMessageKey,
  )

  object Defaults {
    const val WorkerPoolSize = 5u
    const val JobQueueSize = 5u

    const val KafkaConsumerClientID = "install-data-handler" // keep this the same to avoid leaving dead messages
    const val KafkaProducerClientID = "install-data-handler-send"

    inline val InstallDataTriggerTopic
      get() = vdi.lib.kafka.router.KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC

    inline val InstallDataTriggerMessageKey
      get() = vdi.lib.kafka.router.KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY
  }
}
