package vdi.components.kafka

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducerImpl(val kafka: Producer<String, String>) : KafkaProducer {
  override fun send(topic: String, message: KafkaSerializable) {
    send(topic, message.toKafkaMessage())
  }

  override fun send(topic: String, message: KafkaMessage) {
    kafka.send(ProducerRecord(topic, message.key, message.value)).get()
  }
}