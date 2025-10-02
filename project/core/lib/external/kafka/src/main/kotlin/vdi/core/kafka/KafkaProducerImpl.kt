package vdi.core.kafka

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class KafkaProducerImpl(val kafka: Producer<String, String>) : KafkaProducer {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun send(topic: MessageTopic, message: KafkaMessage, wait: Boolean) {
    logger.trace("send(topic={}, message={}, wait={})", topic, message, wait)
    val future = kafka.send(ProducerRecord(topic.value, message.key?.value, message.value))

    if (wait)
      future.get()
  }

  override fun close() {
    logger.trace("close()")
    kafka.close()
  }
}
