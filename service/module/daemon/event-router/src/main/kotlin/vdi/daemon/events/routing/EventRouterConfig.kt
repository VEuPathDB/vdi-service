package vdi.daemon.events.routing

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.kafka.router.KafkaRouterConfig
import vdi.lib.rabbit.RabbitMQConfig

data class EventRouterConfig(
  val rabbitConfig: RabbitMQConfig,
  val s3Bucket:     String,
  val kafkaConfig:  KafkaRouterConfig
) {
  constructor(config: VDIConfig): this(
    RabbitMQConfig(config.rabbit.global),
    config.objectStore.bucketName,
    KafkaRouterConfig(config.daemons?.eventRouter?.kafkaProducerID ?: "event-router", config.kafka, config.lanes)
  )
}

