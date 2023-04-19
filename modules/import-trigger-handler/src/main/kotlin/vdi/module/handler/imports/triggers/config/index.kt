package vdi.module.handler.imports.triggers.config

import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.*
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfigDefaults
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  ImportTriggerHandlerConfig(
    workerPoolSize = env.optUInt(EnvKey.ImportTriggerHandler.WorkerPoolSize)
                        ?: ImportTriggerHandlerConfigDefaults.WorkerPoolSize,
    kafkaConfig    = loadKafkaConfigFromEnvironment(env),
    s3Config       = S3Config(env),
    s3Bucket       = BucketName(env.require(EnvKey.S3.BucketName)),
  )

internal fun loadKafkaConfigFromEnvironment(env: Environment) =
  KafkaConfig(
    consumerConfig          = KafkaConsumerConfig(
      env.require(EnvKey.ImportTriggerHandler.KafkaConsumerClientID),
      env
    ),
    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
                                 ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
                                 ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,
  )

