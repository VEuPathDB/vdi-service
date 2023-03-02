package vdi.components.kafka

interface KafkaPublisher {
  suspend fun publish(topic: String, message: KafkaMessage)
}