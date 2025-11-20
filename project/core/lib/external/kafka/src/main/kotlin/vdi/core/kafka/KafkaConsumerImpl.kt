package vdi.core.kafka

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        MessageTopic,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  @OptIn(DelicateCoroutinesApi::class)
  private val coContext = newFixedThreadPoolContext(8, "kafka-polling")

  override suspend fun receive() = withContext(coContext) {
    kafka.poll(pollDuration.toJavaDuration())
      .map { KafkaMessage(it.key()?.toMessageKey(), it.value()) }
  }

  override fun close() = kafka.close()
}

