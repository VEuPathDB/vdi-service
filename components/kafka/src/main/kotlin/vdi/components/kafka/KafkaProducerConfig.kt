package vdi.components.kafka

import kotlin.time.Duration
import vdi.components.common.util.HostAddress

data class KafkaProducerConfig(
  // bootstrap.servers
  val servers: Array<HostAddress>,

  // buffer.memory
  val bufferMemoryBytes: Long = KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,

  // compression.type
  val compressionType: KafkaCompressionType = KafkaProducerConfigDefaults.COMPRESSION_TYPE,

  // retries
  val sendRetries: Int = KafkaProducerConfigDefaults.SEND_RETRIES,

  // batch.size
  val batchSize: Int = KafkaProducerConfigDefaults.BATCH_SIZE,

  // client.id
  val clientID: String,

  // connections.max.idle.ms
  val connectionsMaxIdle: Duration = KafkaProducerConfigDefaults.CONNECTIONS_MAX_IDLE,

  // delivery.timeout.ms
  val deliveryTimeout: Duration = KafkaProducerConfigDefaults.DELIVERY_TIMEOUT,

  // linger.ms
  val lingerTime: Duration = KafkaProducerConfigDefaults.LINGER_TIME,

  // max.block.ms
  val maxBlockingTimeout: Duration = KafkaProducerConfigDefaults.MAX_BLOCKING_TIMEOUT,

  // max.request.size
  val maxRequestSizeBytes: Int = KafkaProducerConfigDefaults.MAX_REQUEST_SIZE_BYTES,

  // receive.buffer.bytes
  val receiveBufferSizeBytes: Int = KafkaProducerConfigDefaults.RECEIVE_BUFFER_SIZE_BYTES,

  // request.timeout.ms
  val requestTimeout: Duration = KafkaProducerConfigDefaults.REQUEST_TIMEOUT,

  // send.buffer.bytes
  val sendBufferSizeBytes: Int = KafkaProducerConfigDefaults.SEND_BUFFER_SIZE_BYTES,

  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration = KafkaProducerConfigDefaults.RECONNECT_BACKOFF_MAX_TIME,

  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration = KafkaProducerConfigDefaults.RECONNECT_BACKOFF_TIME,

  // retry.backoff.ms
  val retryBackoffTime: Duration = KafkaProducerConfigDefaults.RETRY_BACKOFF_TIME,
)
