package vdi.core.kafka

import kotlin.time.Duration
import vdi.config.loadAndCacheStackConfig
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.kafka.KafkaProducerConfig
import vdi.model.field.HostAddress

data class KafkaProducerConfig(
  // bootstrap.servers
  val servers: List<HostAddress>,

  // buffer.memory
  val bufferMemory: Long?,

  // compression.type
  val compressionType: KafkaCompressionType?,

  // retries
  val sendRetries: Int?,

  // batch.size
  val batchSize: Int?,

  // client.id
  val clientID: String,

  // connections.max.idle.ms
  val connectionsMaxIdle: Duration?,

  // delivery.timeout.ms
  val deliveryTimeout: Duration?,

  // linger.ms
  val lingerTime: Duration?,

  // max.block.ms
  val maxBlockingTimeout: Duration?,

  // max.request.size
  val maxRequestSize: Int?,

  // receive.buffer.bytes
  val receiveBufferSize: Int?,

  // request.timeout.ms
  val requestTimeout: Duration?,

  // send.buffer.bytes
  val sendBufferSize: Int?,

  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration?,

  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration?,

  // retry.backoff.ms
  val retryBackoffTime: Duration?,
) {
  constructor(clientID: String): this(clientID, loadAndCacheStackConfig().vdi.kafka)

  constructor(clientID: String, conf: KafkaConfig)
    : this(clientID, conf.servers.map { it.toHostAddress(DefaultServerPort) }, conf.producers)

  constructor(clientID: String, servers: List<HostAddress>, conf: KafkaProducerConfig?) : this(
    servers                 = servers,
    bufferMemory            = conf?.bufferMemory,
    compressionType         = conf?.compressionType?.let(KafkaCompressionType.Companion::fromString),
    sendRetries             = conf?.sendRetries,
    batchSize               = conf?.batchSize,
    clientID                = clientID,
    connectionsMaxIdle      = conf?.connectionsMaxIdle,
    deliveryTimeout         = conf?.deliveryTimeout,
    lingerTime              = conf?.lingerTime,
    maxBlockingTimeout      = conf?.maxBlockingTimeout,
    maxRequestSize          = conf?.maxRequestSize,
    receiveBufferSize       = conf?.receiveBufferSize,
    requestTimeout          = conf?.requestTimeout,
    sendBufferSize          = conf?.sendBufferSize,
    reconnectBackoffMaxTime = conf?.reconnectBackoffMaxTime,
    reconnectBackoffTime    = conf?.reconnectBackoffTime,
    retryBackoffTime        = conf?.retryBackoffTime
  )
}
