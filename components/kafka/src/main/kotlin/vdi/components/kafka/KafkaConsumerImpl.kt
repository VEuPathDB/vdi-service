package vdi.components.kafka

import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        String,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  override suspend fun receive(): List<KafkaMessage> {
    return kafka.poll(pollDuration.toJavaDuration())
      .records(topic)
      .map { KafkaMessage(it.key(), it.value()) }
  }
}

