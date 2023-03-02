package vdi.components.kafka

/**
 * Kafka Message Consumer
 *
 * Pulls messages from a configured Kafka instance.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 */
interface KafkaConsumer {
  suspend fun receive(topic: String): List<KafkaMessage>
}