package vdi.module.handler.imports.triggers.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.kafka.*
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig


internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  ImportTriggerHandlerConfig(
    workerPoolSize = env.optUInt(EnvKey.ImportTriggerHandler.WorkerPoolSize)
                        ?: ImportTriggerHandlerConfigDefaults.WorkerPoolSize,
    kafkaConfig    = loadKafkaConfigFromEnvironment(env),
    s3Config       = S3Config(

    ),
    s3Bucket       = BucketName(env.require(EnvKey.S3.BucketName)),
    cacheDBConfig  = ,
    handlerAddress = env.reqHostAddress(EnvKey.Handler.HostAddress)
  )

internal fun loadKafkaConfigFromEnvironment(env: Environment) =
  KafkaConfig(
    consumerConfig          = KafkaConsumerConfig(env),
    routerConfig            = KafkaRouterConfig(env),
    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerMessageKey,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerTopic,
  )

