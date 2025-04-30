package vdi.lib.config.vdi

import kotlin.time.Duration

data class KafkaProducerConfig(
  val bufferMemoryBytes: UInt?,
  val compressionType: String?,
  val sendRetries: UInt?,
  val batchSize: UInt?,
  val connectionsMaxIdle: Duration?,
  val deliveryTimeout: Duration?,
  val lingerTime: Duration?,
  val maxBlockingTimeout: Duration?,
  val maxRequestSize: UInt?,
  val receiveBufferSize: Int?,
  val requestTimeout: Duration?,
  val sendBufferSize: Int?,
  val reconnectBackoffMaxTime: Duration?,
  val reconnectBackoffTime: Duration?,
  val retryBackoffTime: Duration?,
)
