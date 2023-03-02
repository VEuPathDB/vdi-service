package vdi.module.events.routing.config

import vdi.components.rabbit.RabbitMQConfig

data class EventRouterConfig(
  val rabbitConfig: RabbitMQConfig,
  val s3Bucket: String,
  val kafkaConfig: KafkaConfig
)

