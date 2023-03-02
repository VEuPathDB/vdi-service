package vdi.components.kafka

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import vdi.components.common.util.HostAddress

data class KafkaConsumerConfig(
  // bootstrap.servers
  val servers: Array<HostAddress>,
  // fetch.min.bytes
  val fetchMinBytes: Int = 1,
  // group.id
  val groupID: String,
  // heartbeat.interval.ms
  val heartbeatInterval: Duration = 3.seconds,
  // session.timeout.ms
  val sessionTimeout: Duration = 45.seconds,
  // auto.offset.reset
  val autoOffsetReset: KafkaOffsetType = KafkaOffsetType.EARLIEST,
  // connections.max.idle.ms
  val connectionsMaxIdle: Duration = 9.minutes,
  // default.api.timeout.ms
  val defaultAPITimeout: Duration = 1.minutes,
  // enable.auto.commit
  val enableAutoCommit: Boolean = true,
  // fetch.max.bytes
  val fetchMaxBytes: Int = 52428800,
  // group.instance.id
  val groupInstanceID: String? = null,
  // max.poll.interval.ms
  val maxPollInterval: Duration = 5.minutes,
  // max.poll.records
  val maxPollRecords: Int = 500,
  // receive.buffer.byte
  val receiveBufferSizeBytes: Int = 65536,
  // request.timeout.ms
  val requestTimeout: Duration = 30.seconds,
  // send.buffer.bytes
  val sendBufferSizeBytes: Int = 131072,
  // auto.commit.interval.ms
  val autoCommitInterval: Duration = 5.seconds,
  // client.id
  val clientID: String,
  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration = 1.seconds,
  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration = 50.milliseconds,
  // retry.backoff.ms
  val retryBackoffTime: Duration = 100.milliseconds,
)