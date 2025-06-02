package vdi.core.kafka

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducerImpl(val kafka: Producer<String, String>) : KafkaProducer {
  override fun send(topic: MessageTopic, message: KafkaSerializable, wait: Boolean) {
    send(topic, message.toKafkaMessage(), wait)
  }

  override fun send(topic: MessageTopic, message: KafkaMessage, wait: Boolean) {
    val future = kafka.send(ProducerRecord(topic.value, message.key?.value, message.value))

    if (wait)
      future.get()
  }

  override fun close() {
    kafka.close()
  }
}
