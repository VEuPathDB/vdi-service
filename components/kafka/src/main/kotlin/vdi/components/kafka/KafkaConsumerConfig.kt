package vdi.components.kafka

import kotlin.time.Duration
import vdi.components.common.util.HostAddress

data class KafkaConsumerConfig(
  // bootstrap.servers
  val servers: Array<HostAddress>,

  // fetch.min.bytes
  val fetchMinBytes: Int = KafkaConsumerConfigDefaults.FetchMinBytes,

  // group.id
  val groupID: String,

  // heartbeat.interval.ms
  val heartbeatInterval: Duration = KafkaConsumerConfigDefaults.HeartbeatInterval,

  // session.timeout.ms
  val sessionTimeout: Duration = KafkaConsumerConfigDefaults.SessionTimeout,

  // auto.offset.reset
  val autoOffsetReset: KafkaOffsetType = KafkaConsumerConfigDefaults.AutoOffsetReset,

  // connections.max.idle.ms
  val connectionsMaxIdle: Duration = KafkaConsumerConfigDefaults.ConnectionsMaxIdle,

  // default.api.timeout.ms
  val defaultAPITimeout: Duration = KafkaConsumerConfigDefaults.DefaultAPITimeout,

  // enable.auto.commit
  val enableAutoCommit: Boolean = KafkaConsumerConfigDefaults.EnableAutoCommit,

  // fetch.max.bytes
  val fetchMaxBytes: Int = KafkaConsumerConfigDefaults.FetchMaxBytes,

  // group.instance.id
  val groupInstanceID: String? = KafkaConsumerConfigDefaults.GroupInstanceID,

  // max.poll.interval.ms
  val maxPollInterval: Duration = KafkaConsumerConfigDefaults.MaxPollInterval,

  // max.poll.records
  val maxPollRecords: Int = KafkaConsumerConfigDefaults.MaxPollRecords,

  // receive.buffer.byte
  val receiveBufferSizeBytes: Int = KafkaConsumerConfigDefaults.ReceiveBufferSizeBytes,

  // request.timeout.ms
  val requestTimeout: Duration = KafkaConsumerConfigDefaults.RequestTimeout,

  // send.buffer.bytes
  val sendBufferSizeBytes: Int = KafkaConsumerConfigDefaults.SendBufferSizeBytes,

  // auto.commit.interval.ms
  val autoCommitInterval: Duration = KafkaConsumerConfigDefaults.AutoCommitInterval,

  // client.id
  val clientID: String,

  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration = KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,

  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration = KafkaConsumerConfigDefaults.ReconnectBackoffTime,

  // retry.backoff.ms
  val retryBackoffTime: Duration = KafkaConsumerConfigDefaults.RetryBackoffTime,

  val pollDuration: Duration = KafkaConsumerConfigDefaults.PollDuration,
)

