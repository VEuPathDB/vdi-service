package org.veupathdb.vdi.lib.kafka

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.util.HostAddress
import org.veupathdb.vdi.lib.kafka.KafkaCompressionType
import kotlin.time.Duration

data class KafkaProducerConfig(
  // bootstrap.servers
  val servers: List<HostAddress>,

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
) {
  constructor(env: Environment) : this(
    servers                 = env.reqHostAddresses(EnvKey.Kafka.Servers),
    bufferMemoryBytes       = env.optLong(EnvKey.Kafka.Producer.BufferMemoryBytes)
      ?: KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,
    compressionType         = env.optional(EnvKey.Kafka.Producer.CompressionType)
      ?.let(KafkaCompressionType::fromString)
      ?: KafkaProducerConfigDefaults.COMPRESSION_TYPE,
    sendRetries             = env.optInt(EnvKey.Kafka.Producer.SendRetries)
      ?: KafkaProducerConfigDefaults.SEND_RETRIES,
    batchSize               = env.optInt(EnvKey.Kafka.Producer.BatchSize)
      ?: KafkaProducerConfigDefaults.BATCH_SIZE,
    clientID                = env.require(EnvKey.Kafka.Producer.ClientID),
    connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Producer.ConnectionsMaxIdle)
      ?: KafkaProducerConfigDefaults.CONNECTIONS_MAX_IDLE,
    deliveryTimeout         = env.optDuration(EnvKey.Kafka.Producer.DeliveryTimeout)
      ?: KafkaProducerConfigDefaults.DELIVERY_TIMEOUT,
    lingerTime              = env.optDuration(EnvKey.Kafka.Producer.LingerTime)
      ?: KafkaProducerConfigDefaults.LINGER_TIME,
    maxBlockingTimeout      = env.optDuration(EnvKey.Kafka.Producer.MaxBlockingTimeout)
      ?: KafkaProducerConfigDefaults.MAX_BLOCKING_TIMEOUT,
    maxRequestSizeBytes     = env.optInt(EnvKey.Kafka.Producer.MaxRequestSizeBytes)
      ?: KafkaProducerConfigDefaults.MAX_REQUEST_SIZE_BYTES,
    receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Producer.ReceiveBufferSizeBytes)
      ?: KafkaProducerConfigDefaults.RECEIVE_BUFFER_SIZE_BYTES,
    requestTimeout          = env.optDuration(EnvKey.Kafka.Producer.RequestTimeout)
      ?: KafkaProducerConfigDefaults.REQUEST_TIMEOUT,
    sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Producer.SendBufferSizeBytes)
      ?: KafkaProducerConfigDefaults.SEND_BUFFER_SIZE_BYTES,
    reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffMaxTime)
      ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_MAX_TIME,
    reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffTime)
      ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_TIME,
    retryBackoffTime        = env.optDuration(EnvKey.Kafka.Producer.RetryBackoffTime)
      ?: KafkaProducerConfigDefaults.RETRY_BACKOFF_TIME

  )

  constructor(env: Environment, clientID: String) : this(
    servers                 = env.reqHostAddresses(EnvKey.Kafka.Servers),
    bufferMemoryBytes       = env.optLong(EnvKey.Kafka.Producer.BufferMemoryBytes)
      ?: KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,
    compressionType         = env.optional(EnvKey.Kafka.Producer.CompressionType)
      ?.let(KafkaCompressionType::fromString)
      ?: KafkaProducerConfigDefaults.COMPRESSION_TYPE,
    sendRetries             = env.optInt(EnvKey.Kafka.Producer.SendRetries)
      ?: KafkaProducerConfigDefaults.SEND_RETRIES,
    batchSize               = env.optInt(EnvKey.Kafka.Producer.BatchSize)
      ?: KafkaProducerConfigDefaults.BATCH_SIZE,
    clientID                = clientID,
    connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Producer.ConnectionsMaxIdle)
      ?: KafkaProducerConfigDefaults.CONNECTIONS_MAX_IDLE,
    deliveryTimeout         = env.optDuration(EnvKey.Kafka.Producer.DeliveryTimeout)
      ?: KafkaProducerConfigDefaults.DELIVERY_TIMEOUT,
    lingerTime              = env.optDuration(EnvKey.Kafka.Producer.LingerTime)
      ?: KafkaProducerConfigDefaults.LINGER_TIME,
    maxBlockingTimeout      = env.optDuration(EnvKey.Kafka.Producer.MaxBlockingTimeout)
      ?: KafkaProducerConfigDefaults.MAX_BLOCKING_TIMEOUT,
    maxRequestSizeBytes     = env.optInt(EnvKey.Kafka.Producer.MaxRequestSizeBytes)
      ?: KafkaProducerConfigDefaults.MAX_REQUEST_SIZE_BYTES,
    receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Producer.ReceiveBufferSizeBytes)
      ?: KafkaProducerConfigDefaults.RECEIVE_BUFFER_SIZE_BYTES,
    requestTimeout          = env.optDuration(EnvKey.Kafka.Producer.RequestTimeout)
      ?: KafkaProducerConfigDefaults.REQUEST_TIMEOUT,
    sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Producer.SendBufferSizeBytes)
      ?: KafkaProducerConfigDefaults.SEND_BUFFER_SIZE_BYTES,
    reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffMaxTime)
      ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_MAX_TIME,
    reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffTime)
      ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_TIME,
    retryBackoffTime        = env.optDuration(EnvKey.Kafka.Producer.RetryBackoffTime)
      ?: KafkaProducerConfigDefaults.RETRY_BACKOFF_TIME

  )

}
