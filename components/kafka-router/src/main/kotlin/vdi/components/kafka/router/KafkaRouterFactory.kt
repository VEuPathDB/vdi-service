package vdi.components.kafka.router

import vdi.components.kafka.KafkaProducer

class KafkaRouterFactory(private val config: KafkaRouterConfig) {
  fun newKafkaRouter() = KafkaRouter(config, KafkaProducer(config.producerConfig))
}