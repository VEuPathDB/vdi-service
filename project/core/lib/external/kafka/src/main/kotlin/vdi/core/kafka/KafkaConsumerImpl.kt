package vdi.core.kafka

import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        MessageTopic,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  override fun receive() =
    kafka.poll(pollDuration.toJavaDuration())
      .map { KafkaMessage(it.key()?.toMessageKey(), it.value()) }

  override fun close() = kafka.close()
}

