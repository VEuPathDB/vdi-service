package vdi.component.kafka.router

import vdi.component.kafka.KafkaProducer

class KafkaRouterFactory(private val config: KafkaRouterConfig) {
  fun newKafkaRouter() = KafkaRouter(config, KafkaProducer(config.producerConfig))
}