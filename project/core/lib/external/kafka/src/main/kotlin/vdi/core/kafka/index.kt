package vdi.core.kafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import vdi.core.health.RemoteDependencies
import vdi.model.field.HostAddress
import org.apache.kafka.clients.consumer.KafkaConsumer as KConsumer
import org.apache.kafka.clients.producer.KafkaProducer as KProducer

private val knownServers = HashSet<HostAddress>(1)
private fun init(servers: Iterable<HostAddress>) {
  synchronized(knownServers) {
    for (server in servers) {
      if (server !in knownServers) {
        knownServers.add(server)
        RemoteDependencies.register("Kafka $server", server.host, server.port)
      }
    }
  }
}

fun KafkaConsumer(topic: MessageTopic, config: KafkaConsumerConfig): KafkaConsumer {
  init(config.servers)
  return KafkaConsumerImpl(
    topic,
    config.pollDuration,
    StringDeserializer()
      .let { KConsumer(config.toProperties(), it, it).apply { subscribe(listOf(topic.value)) } }
  )
}

fun KafkaProducer(config: KafkaProducerConfig): KafkaProducer {
  init(config.servers)
  return KafkaProducerImpl(StringSerializer().let { KProducer(config.toProperties(), it, it) })
}
