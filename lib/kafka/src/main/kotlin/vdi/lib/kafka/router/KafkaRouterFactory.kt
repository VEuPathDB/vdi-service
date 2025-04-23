package vdi.lib.kafka.router

import vdi.lib.kafka.KafkaProducer

class KafkaRouterFactory(private val config: KafkaRouterConfig) {
  fun newKafkaRouter() = vdi.lib.kafka.router.KafkaRouter(config, KafkaProducer(config.producerConfig))
}
