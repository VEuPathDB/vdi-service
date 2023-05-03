package vdi.module.handler.share.trigger.config

import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  ShareTriggerHandlerConfig(
    workerPoolSize = env.optUInt(EnvKey.ShareTriggerHandler.WorkerPoolSize)
      ?: ShareTriggerHandlerConfigDefaults.WorkerPoolSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.ShareTriggerHandler.KafkaConsumerClientID),
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    shareTriggerTopic = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,

    shareTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
      ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY
  )

