package vdi.module.events.routing.config

import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.rabbit.RabbitMQConfig

data class EventRouterConfig(
  val rabbitConfig: RabbitMQConfig,
  val s3Bucket: String,
  val kafkaConfig: KafkaRouterConfig
)

