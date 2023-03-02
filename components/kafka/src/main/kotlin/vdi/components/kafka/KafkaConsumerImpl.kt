package vdi.components.kafka

import org.apache.kafka.common.TopicPartition
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        String,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  override fun receive(): List<KafkaMessage> {
    return kafka.poll(pollDuration.toJavaDuration())
      .map { KafkaMessage(it.key(), it.value()) }
  }
}

