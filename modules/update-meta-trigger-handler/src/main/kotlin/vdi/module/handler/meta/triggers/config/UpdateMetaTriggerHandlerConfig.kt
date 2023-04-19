package vdi.module.handler.meta.triggers.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig

data class UpdateMetaTriggerHandlerConfig(
  val workerPoolSize: UInt,
  val kafkaConsumerConfig: KafkaConsumerConfig,
  val kafkaRouterConfig: KafkaRouterConfig,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
)

