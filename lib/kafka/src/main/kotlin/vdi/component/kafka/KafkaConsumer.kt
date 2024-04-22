package vdi.component.kafka

import kotlin.time.Duration

/**
 * Kafka Message Consumer
 *
 * Pulls messages from a configured Kafka instance.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface KafkaConsumer {

  /**
   * Kafka polling duration.
   *
   * This configures how long the [receive] function should wait for messages
   * when polling the Kafka instance.
   */
  val pollDuration: Duration

  /**
   * Subscribed Kafka topic.
   *
   * This is the topic that this [KafkaConsumer] listens to when polling Kafka
   * for messages.
   */
  val topic: String

  /**
   * Polls Kafka for messages.
   *
   * This method will wait for the configured
   * [poll duration][pollDuration] before returning a list
   * of messages that were available in Kafka under the configured topic after
   * that duration.
   *
   * If no messages are available in Kafka, the returned list will be empty.
   *
   * @return A list of zero or more messages that were available in Kafka.
   */
  fun receive(): List<KafkaMessage>

  /**
   * Closes the Kafka consumer connection.
   */
  fun close()
}
