package vdi.module.handler.meta.triggers.config

import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optUInt
import org.veupathdb.vdi.lib.common.env.require
import org.veupathdb.vdi.lib.db.cache.CacheDBConfig
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.s3.datasets.util.S3Config

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  UpdateMetaTriggerHandlerConfig(
    workerPoolSize = env.optUInt(EnvKey.UpdateMetaTriggerHandler.WorkerPoolSize)
      ?: UpdateMetaTriggerHandlerConfigDefaults.WorkerPoolSize,
    kafkaConsumerConfig = KafkaConsumerConfig(env),
    kafkaRouterConfig = KafkaRouterConfig(env),
    s3Config = S3Config(env),
    s3Bucket = BucketName(env.require(EnvKey.S3.BucketName)),
    cacheDBConfig = CacheDBConfig(env),
  )
