package vdi.components.kafka

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import vdi.components.common.util.HostAddress

data class KafkaProducerConfig(
  // bootstrap.servers
  val servers: Array<HostAddress>,
  // buffer.memory
  val bufferMemoryBytes: Long = 33554432L,
  // compression.type
  val compressionType: KafkaCompressionType = KafkaCompressionType.NONE,
  // retries
  val sendRetries: Int = 2147483647,
  // batch.size
  val batchSize: Int = 16384,
  // client.id
  val clientID: String,
  // connections.max.idle.ms
  val connectionsMaxIdle: Duration = 9.minutes,
  // delivery.timeout.ms
  val deliveryTimeout: Duration = 2.minutes,
  // linger.ms
  val lingerTime: Duration = 0.milliseconds,
  // max.block.ms
  val maxBlockingTimeout: Duration = 1.minutes,
  // max.request.size
  val maxRequestSizeBytes: Int = 1048576,
  // receive.buffer.bytes
  val receiveBufferSizeBytes: Int = 32768,
  // request.timeout.ms
  val requestTimeout: Duration = 30.seconds,
  // send.buffer.bytes
  val sendBufferSizeBytes: Int = 131072,
  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration = 1.seconds,
  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration = 50.milliseconds,
  // retry.backoff.ms
  val retryBackoffTime: Duration = 100.milliseconds,
)

