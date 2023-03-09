package vdi.module.handler.imports.triggers.config

data class ImportTriggerHandlerConfig(
  val kafkaConfig: KafkaConfig,
  val s3Bucket: String,
)

