package org.veupathdb.vdi.lib.kafka.router

import org.veupathdb.vdi.lib.kafka.KafkaProducer

class KafkaRouterFactory(private val config: KafkaRouterConfig) {
  fun newKafkaRouter() = KafkaRouter(config, KafkaProducer(config.producerConfig))
}