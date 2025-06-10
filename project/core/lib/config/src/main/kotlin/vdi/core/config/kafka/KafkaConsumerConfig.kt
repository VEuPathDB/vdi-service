package vdi.core.config.kafka

import kotlin.time.Duration

data class KafkaConsumerConfig(
  val autoCommitInterval: Duration?,
  val autoOffsetReset: String?,
  val connectionsMaxIdle: Duration?,
  val groupId: String?,
  val groupInstanceId: String?,
  val defaultApiTimeout: Duration?,
  val enableAutoCommit: Boolean?,
  val fetchMaxBytes: Int?,
  val fetchMinBytes: Int?,
  val heartbeatInterval: Duration?,
  val maxPollInterval: Duration?,
  val maxPollRecords: Int?,
  val pollDuration: Duration?,
  val receiveBufferSize: Int?,
  val reconnectBackoffMaxTime: Duration?,
  val reconnectBackoffTime: Duration?,
  val requestTimeout: Duration?,
  val retryBackoffTime: Duration?,
  val sendBufferSize: Int?,
  val sessionTimeout: Duration?,
)
