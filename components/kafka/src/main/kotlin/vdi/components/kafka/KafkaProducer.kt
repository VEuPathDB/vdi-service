package vdi.components.kafka

/**
 * Kafka Message Producer
 *
 * Sends messages to a configured Kafka instance.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface KafkaProducer {

  /**
   * Sends a message to Kafka.
   *
   * This method will wait for the message to be confirmed by the Kafka server
   * before returning.
   *
   * @param topic Topic for the message being sent.
   *
   * @param message Message to send.
   */
  fun send(topic: String, message: KafkaMessage, wait: Boolean = false)

  /**
   * Sends a message to Kafka.
   *
   * This method will wait for the message to be confirmed by the Kafka server
   * before returning.
   *
   * @param topic Topic for the message being sent.
   *
   * @param message Message to send.
   */
  fun send(topic: String, message: KafkaSerializable, wait: Boolean = false)

  /**
   * Closes the producer connection.
   */
  fun close()
}

