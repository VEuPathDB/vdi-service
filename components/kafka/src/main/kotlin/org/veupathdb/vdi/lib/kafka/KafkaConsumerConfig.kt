package org.veupathdb.vdi.lib.kafka

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.util.HostAddress
import kotlin.time.Duration

data class KafkaConsumerConfig(
  // bootstrap.servers
  val servers: List<HostAddress>,

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
) {
  constructor(clientID: String, env: Environment) : this(
    servers                 = env.reqHostAddresses(EnvKey.Kafka.Servers),
    fetchMinBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMinBytes)
                                 ?: KafkaConsumerConfigDefaults.FetchMinBytes,
    groupID                 = env.require(EnvKey.Kafka.Consumer.GroupID),
    heartbeatInterval       = env.optDuration(EnvKey.Kafka.Consumer.HeartbeatInterval)
                                 ?: KafkaConsumerConfigDefaults.HeartbeatInterval,
    sessionTimeout          = env.optDuration(EnvKey.Kafka.Consumer.SessionTimeout)
                                 ?: KafkaConsumerConfigDefaults.SessionTimeout,
    autoOffsetReset         = env.optional(EnvKey.Kafka.Consumer.AutoOffsetReset)
                                 ?.let(KafkaOffsetType::fromString)
                                 ?: KafkaConsumerConfigDefaults.AutoOffsetReset,
    connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Consumer.ConnectionsMaxIdle)
                                 ?: KafkaConsumerConfigDefaults.ConnectionsMaxIdle,
    defaultAPITimeout       = env.optDuration(EnvKey.Kafka.Consumer.DefaultAPITimeout)
                                 ?: KafkaConsumerConfigDefaults.DefaultAPITimeout,
    enableAutoCommit        = env.optBool(EnvKey.Kafka.Consumer.EnableAutoCommit)
                                 ?: KafkaConsumerConfigDefaults.EnableAutoCommit,
    fetchMaxBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMaxBytes)
                                 ?: KafkaConsumerConfigDefaults.FetchMaxBytes,
    groupInstanceID         = env.optional(EnvKey.Kafka.Consumer.GroupInstanceID)
                                 ?: KafkaConsumerConfigDefaults.GroupInstanceID,
    maxPollInterval         = env.optDuration(EnvKey.Kafka.Consumer.MaxPollInterval)
                                 ?: KafkaConsumerConfigDefaults.MaxPollInterval,
    maxPollRecords          = env.optInt(EnvKey.Kafka.Consumer.MaxPollRecords)
                                 ?: KafkaConsumerConfigDefaults.MaxPollRecords,
    receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Consumer.ReceiveBufferSizeBytes)
                                 ?: KafkaConsumerConfigDefaults.ReceiveBufferSizeBytes,
    requestTimeout          = env.optDuration(EnvKey.Kafka.Consumer.RequestTimeout)
                                 ?: KafkaConsumerConfigDefaults.RequestTimeout,
    sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Consumer.SendBufferSizeBytes)
                                 ?: KafkaConsumerConfigDefaults.SendBufferSizeBytes,
    autoCommitInterval      = env.optDuration(EnvKey.Kafka.Consumer.AutoCommitInterval)
                                 ?: KafkaConsumerConfigDefaults.AutoCommitInterval,
    clientID                = clientID,
    reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffMaxTime)
                                 ?: KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,
    reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffTime)
                                 ?: KafkaConsumerConfigDefaults.ReconnectBackoffTime,
    retryBackoffTime        = env.optDuration(EnvKey.Kafka.Consumer.RetryBackoffTime)
                                 ?: KafkaConsumerConfigDefaults.RetryBackoffTime,
    pollDuration            = env.optDuration(EnvKey.Kafka.Consumer.PollDuration)
                                 ?: KafkaConsumerConfigDefaults.PollDuration

  )
}

