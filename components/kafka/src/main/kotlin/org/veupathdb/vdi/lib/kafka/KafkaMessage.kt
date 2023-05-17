package org.veupathdb.vdi.lib.kafka

/**
 * Kafka Message
 *
 * Represents a message being sent to Kafka.
 */
data class KafkaMessage(

  /**
   * Message Key
   */
  val key: String?,

  /**
   * Message Value
   */
  val value: String,
)