package vdi.daemon.events.routing

import vdi.core.config.vdi.VDIConfig
import vdi.core.kafka.router.KafkaRouterConfig
import vdi.core.rabbit.RabbitMQConfig

data class EventRouterConfig(
  val rabbitConfig: RabbitMQConfig,
  val s3Bucket:     String,
  val kafkaConfig:  KafkaRouterConfig
) {
  constructor(config: VDIConfig): this(
    RabbitMQConfig(config.rabbit),
    config.objectStore.bucketName,
    KafkaRouterConfig(config.daemons.eventRouter.kafkaProducerID, config.kafka, config.lanes)
  )
}

