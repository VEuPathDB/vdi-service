package vdi.component.kafka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        String,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  override suspend fun receive(): List<KafkaMessage> =
    withContext(Dispatchers.IO) { kafka.poll(pollDuration.toJavaDuration()) }
      .map { KafkaMessage(it.key(), it.value()) }

  override fun close() {
    kafka.close()
  }
}

