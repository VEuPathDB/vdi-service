package org.veupathdb.vdi.lib.kafka

import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.apache.kafka.clients.consumer.Consumer as KConsumer

internal class KafkaConsumerImpl(
  override val topic:        String,
  override val pollDuration: Duration,
  private  val kafka:        KConsumer<String, String>
) : KafkaConsumer {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun receive(): List<KafkaMessage> {
    log.trace("receive()")

    log.trace("polling kafka for messages on topic {}", topic)
    return kafka.poll(pollDuration.toJavaDuration())
      .map { KafkaMessage(it.key(), it.value()) }
  }

  override fun close() {
    kafka.close()
  }
}

