package vdi.components.kafka

interface KafkaSerializable {
  fun toKafkaMessage(): KafkaMessage
}