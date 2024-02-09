package vdi.module.handler.share.trigger

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

data class ShareTriggerHandlerConfig(
  val workerPoolSize:         UInt,
  val workQueueSize:          UInt,
  val kafkaConsumerConfig:    KafkaConsumerConfig,
  val s3Config:               S3Config,
  val s3Bucket:               BucketName,
  val shareTriggerTopic:      String,
  val shareTriggerMessageKey: String,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    workerPoolSize = env.optUInt(EnvKey.ShareTriggerHandler.WorkerPoolSize)
      ?: Defaults.WorkerPoolSize,

    workQueueSize = env.optUInt(EnvKey.ShareTriggerHandler.WorkQueueSize)
      ?: Defaults.WorkQueueSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.optional(EnvKey.ShareTriggerHandler.KafkaConsumerClientID) ?: "share-handler",
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    shareTriggerTopic = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,

    shareTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY
  )

  object Defaults {
    const val WorkerPoolSize = 10u
    const val WorkQueueSize = 10u
  }
}
