package vdi.core.config.kafka

import kotlin.time.Duration

data class KafkaProducerConfig(
  val bufferMemory: Long?,
  val compressionType: String?,
  val sendRetries: Int?,
  val batchSize: Int?,
  val connectionsMaxIdle: Duration?,
  val deliveryTimeout: Duration?,
  val lingerTime: Duration?,
  val maxBlockingTimeout: Duration?,
  val maxRequestSize: Int?,
  val receiveBufferSize: Int?,
  val requestTimeout: Duration?,
  val sendBufferSize: Int?,
  val reconnectBackoffMaxTime: Duration?,
  val reconnectBackoffTime: Duration?,
  val retryBackoffTime: Duration?,
)
