package vdi.components.kafka

/**
 * Kafka Serializable
 *
 * Represents a type that can be serialized into a [KafkaMessage] instance to be
 * sent via Kafka.
 *
 * @author Elizabeth Paige Harper - http://github.com/foxcapades
 */
interface KafkaSerializable {

  /**
   * Returns a [KafkaMessage] representing the implementing type's value.
   */
  fun toKafkaMessage(): KafkaMessage
}