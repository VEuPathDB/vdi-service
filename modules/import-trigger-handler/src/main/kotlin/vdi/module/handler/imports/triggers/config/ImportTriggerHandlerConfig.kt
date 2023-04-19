package vdi.module.handler.imports.triggers.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName

data class ImportTriggerHandlerConfig(
  val workerPoolSize: UInt,
  val kafkaConfig: KafkaConfig,
  val s3Config: S3Config,
  val s3Bucket: BucketName,
)
