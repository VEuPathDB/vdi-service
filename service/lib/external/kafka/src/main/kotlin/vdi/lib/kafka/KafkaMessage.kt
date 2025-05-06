package vdi.lib.kafka

/**
 * Kafka Message
 *
 * Represents a message being sent to Kafka.
 */
data class KafkaMessage(

  /**
   * Message Key
   */
  val key: MessageKey?,

  /**
   * Message Value
   */
  val value: String,
)
