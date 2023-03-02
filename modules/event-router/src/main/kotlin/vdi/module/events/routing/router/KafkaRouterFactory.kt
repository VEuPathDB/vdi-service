package vdi.module.events.routing.router

import vdi.components.kafka.KafkaProducer
import vdi.module.events.routing.config.KafkaConfig

internal class KafkaRouterFactory(private val config: KafkaConfig) {
  fun newKafkaRouter() = KafkaRouter(config, KafkaProducer(config.producerConfig))
}