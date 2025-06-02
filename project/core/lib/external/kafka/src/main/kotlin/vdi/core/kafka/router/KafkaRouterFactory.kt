package vdi.core.kafka.router

import vdi.core.kafka.KafkaProducer

class KafkaRouterFactory(private val config: KafkaRouterConfig) {
  fun newKafkaRouter() = KafkaRouter(config, KafkaProducer(config.producerConfig))
}
