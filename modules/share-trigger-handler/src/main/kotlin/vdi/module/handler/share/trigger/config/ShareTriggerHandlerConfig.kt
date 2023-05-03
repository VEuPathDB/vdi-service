package vdi.module.handler.share.trigger.config

import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.kafka.KafkaConsumerConfig

data class ShareTriggerHandlerConfig(
  val workerPoolSize:         UInt,
  val kafkaConsumerConfig:    KafkaConsumerConfig,
  val s3Config:               S3Config,
  val s3Bucket:               BucketName,
  val shareTriggerTopic:      String,
  val shareTriggerMessageKey: String,
)
