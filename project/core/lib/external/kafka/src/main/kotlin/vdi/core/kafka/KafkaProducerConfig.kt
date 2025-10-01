package vdi.core.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.record.CompressionType
import java.util.Properties
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.kafka.KafkaProducerConfig
import vdi.model.field.HostAddress

data class KafkaProducerConfig(
  /**
   * `bootstrap.servers`
   */
  val servers: List<HostAddress>,

  /**
   * `buffer.memory`
   */
  val bufferMemory: Long,

  /**
   * `compression.type`
   */
  val compressionType: CompressionType,

  /**
   * `retries`
   */
  val sendRetries: Int,

  /**
   * `batch.size`
   */
  val batchSize: Int,

  /**
   * `client.id`
   */
  val clientID: String,

  /**
   * `connections.max.idle.ms`
   */
  val connectionsMaxIdle: Duration,

  /**
   * `delivery.timeout.ms`
   */
  val deliveryTimeout: Duration,

  /**
   * `linger.ms`
   */
  val lingerTime: Duration,

  /**
   * `max.block.ms`
   */
  val maxBlockingTimeout: Duration,

  /**
   * `max.request.size`
   */
  val maxRequestSize: Int,

  /**
   * `receive.buffer.bytes`
   */
  val receiveBufferSize: Int,

  /**
   * `request.timeout.ms`
   */
  val requestTimeout: Duration,

  /**
   * `send.buffer.bytes`
   */
  val sendBufferSize: Int,

  /**
   * `reconnect.backoff.max.ms`
   */
  val reconnectBackoffMaxTime: Duration,

  /**
   * `reconnect.backoff.ms`
   */
  val reconnectBackoffTime: Duration,

  /**
   * `retry.backoff.ms`
   */
  val retryBackoffTime: Duration,
) {
  constructor(clientID: String): this(clientID, loadAndCacheStackConfig().vdi.kafka)

  constructor(clientID: String, conf: KafkaConfig)
    : this(clientID, conf.servers.map { it.toHostAddress(DefaultServerPort) }, conf.producers)

  constructor(clientID: String, servers: List<HostAddress>, conf: KafkaProducerConfig?) : this(
    servers                 = servers,
    bufferMemory            = conf?.bufferMemory ?: Defaults.BufferMemoryBytes,
    compressionType         = conf?.compressionType?.let(CompressionType::forName) ?: Defaults.CompressionType,
    sendRetries             = conf?.sendRetries ?: Defaults.SendRetries,
    batchSize               = conf?.batchSize ?: Defaults.BatchSize,
    clientID                = clientID,
    connectionsMaxIdle      = conf?.connectionsMaxIdle ?: Defaults.ConnectionsMaxIdle,
    deliveryTimeout         = conf?.deliveryTimeout ?: Defaults.DeliveryTimeout,
    lingerTime              = conf?.lingerTime ?: Defaults.LingerTime,
    maxBlockingTimeout      = conf?.maxBlockingTimeout ?: Defaults.MaxBlockingTimeout,
    maxRequestSize          = conf?.maxRequestSize ?: Defaults.MaxRequestSize,
    receiveBufferSize       = conf?.receiveBufferSize ?: Defaults.ReceiveBufferSize,
    requestTimeout          = conf?.requestTimeout ?: Defaults.RequestTimeout,
    sendBufferSize          = conf?.sendBufferSize ?: Defaults.SendBufferSize,
    reconnectBackoffMaxTime = conf?.reconnectBackoffMaxTime ?: Defaults.ReconnectBackoffMaxTime,
    reconnectBackoffTime    = conf?.reconnectBackoffTime ?: Defaults.ReconnectBackoffTime,
    retryBackoffTime        = conf?.retryBackoffTime ?: Defaults.RetryBackoffTime,
  )

  fun toProperties() = Properties()
    .apply {
      setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers.joinToString(",") { it.toString() })
      setProperty(ProducerConfig.CLIENT_ID_CONFIG, clientID)
      setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory.toString())
      setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType.toString())
      setProperty(ProducerConfig.RETRIES_CONFIG, sendRetries.toString())
      setProperty(ProducerConfig.BATCH_SIZE_CONFIG, batchSize.toString())
      setProperty(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionsMaxIdle.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.LINGER_MS_CONFIG, lingerTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockingTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize.toString())
      setProperty(ProducerConfig.RECEIVE_BUFFER_CONFIG, receiveBufferSize.toString())
      setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.SEND_BUFFER_CONFIG, sendBufferSize.toString())
      setProperty(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, reconnectBackoffMaxTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffTime.inWholeMilliseconds.toString())
    }

  object Defaults {
    inline val BufferMemoryBytes
      get() = 33554432L

    inline val CompressionType
      get() = org.apache.kafka.common.record.CompressionType.NONE

    inline val SendRetries
      get() = 2147483647

    inline val BatchSize
      get() = 16384

    inline val ConnectionsMaxIdle
      get() = 9.minutes

    inline val DeliveryTimeout
      get() = 2.minutes

    inline val LingerTime
      get() = 5.milliseconds

    inline val MaxBlockingTimeout
      get() = 1.minutes

    inline val MaxRequestSize
      get() = 1048576

    inline val ReceiveBufferSize
      get() = 32768

    inline val RequestTimeout
      get() = 30.seconds

    inline val SendBufferSize
      get() = 131072

    inline val ReconnectBackoffMaxTime
      get() = 1.seconds

    inline val ReconnectBackoffTime
      get() = 50.milliseconds

    inline val RetryBackoffTime
      get() = 100.milliseconds
  }
}
