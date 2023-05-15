package vdi.module.handler.delete.soft.config

import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  SoftDeleteTriggerHandlerConfig(
    workerPoolSize = env.optUInt(EnvKey.SoftDeleteTriggerHandler.WorkerPoolSize)
      ?: SoftDeleteTriggerHandlerConfigDefaults.WorkerPoolSize,

    kafkaConsumerConfig = KafkaConsumerConfig(
      env.require(EnvKey.SoftDeleteTriggerHandler.KafkaConsumerClientID),
      env
    ),

    s3Config = S3Config(env),

    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),

    softDeleteTriggerTopic = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,

    softDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
      ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY
  )