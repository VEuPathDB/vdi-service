package vdi.lib.kafka

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.util.HostAddress
import vdi.lib.env.EnvKey
import kotlin.time.Duration

data class KafkaConsumerConfig(
  // bootstrap.servers
  val servers: List<HostAddress>,

  // fetch.min.bytes
  val fetchMinBytes: Int = vdi.lib.kafka.KafkaConsumerConfigDefaults.FetchMinBytes,

  // group.id
  val groupID: String,

  // heartbeat.interval.ms
  val heartbeatInterval: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.HeartbeatInterval,

  // session.timeout.ms
  val sessionTimeout: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.SessionTimeout,

  // auto.offset.reset
  val autoOffsetReset: vdi.lib.kafka.KafkaOffsetType = vdi.lib.kafka.KafkaConsumerConfigDefaults.AutoOffsetReset,

  // connections.max.idle.ms
  val connectionsMaxIdle: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.ConnectionsMaxIdle,

  // default.api.timeout.ms
  val defaultAPITimeout: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.DefaultAPITimeout,

  // enable.auto.commit
  val enableAutoCommit: Boolean = vdi.lib.kafka.KafkaConsumerConfigDefaults.EnableAutoCommit,

  // fetch.max.bytes
  val fetchMaxBytes: Int = vdi.lib.kafka.KafkaConsumerConfigDefaults.FetchMaxBytes,

  // group.instance.id
  val groupInstanceID: String? = vdi.lib.kafka.KafkaConsumerConfigDefaults.GroupInstanceID,

  // max.poll.interval.ms
  val maxPollInterval: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.MaxPollInterval,

  // max.poll.records
  val maxPollRecords: Int = vdi.lib.kafka.KafkaConsumerConfigDefaults.MaxPollRecords,

  // receive.buffer.byte
  val receiveBufferSizeBytes: Int = vdi.lib.kafka.KafkaConsumerConfigDefaults.ReceiveBufferSizeBytes,

  // request.timeout.ms
  val requestTimeout: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.RequestTimeout,

  // send.buffer.bytes
  val sendBufferSizeBytes: Int = vdi.lib.kafka.KafkaConsumerConfigDefaults.SendBufferSizeBytes,

  // auto.commit.interval.ms
  val autoCommitInterval: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.AutoCommitInterval,

  // client.id
  val clientID: String,

  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,

  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.ReconnectBackoffTime,

  // retry.backoff.ms
  val retryBackoffTime: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.RetryBackoffTime,

  val pollDuration: Duration = vdi.lib.kafka.KafkaConsumerConfigDefaults.PollDuration,
) {
  constructor(clientID: String, env: Environment) : this(
    servers                 = env.reqHostAddresses(EnvKey.Kafka.Servers),
    fetchMinBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMinBytes)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.FetchMinBytes,
    groupID                 = env.require(EnvKey.Kafka.Consumer.GroupID),
    heartbeatInterval       = env.optDuration(EnvKey.Kafka.Consumer.HeartbeatInterval)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.HeartbeatInterval,
    sessionTimeout          = env.optDuration(EnvKey.Kafka.Consumer.SessionTimeout)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.SessionTimeout,
    autoOffsetReset         = env.optional(EnvKey.Kafka.Consumer.AutoOffsetReset)
                                 ?.let(vdi.lib.kafka.KafkaOffsetType.Companion::fromString)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.AutoOffsetReset,
    connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Consumer.ConnectionsMaxIdle)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.ConnectionsMaxIdle,
    defaultAPITimeout       = env.optDuration(EnvKey.Kafka.Consumer.DefaultAPITimeout)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.DefaultAPITimeout,
    enableAutoCommit        = env.optBool(EnvKey.Kafka.Consumer.EnableAutoCommit)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.EnableAutoCommit,
    fetchMaxBytes           = env.optInt(EnvKey.Kafka.Consumer.FetchMaxBytes)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.FetchMaxBytes,
    groupInstanceID         = env.optional(EnvKey.Kafka.Consumer.GroupInstanceID)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.GroupInstanceID,
    maxPollInterval         = env.optDuration(EnvKey.Kafka.Consumer.MaxPollInterval)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.MaxPollInterval,
    maxPollRecords          = env.optInt(EnvKey.Kafka.Consumer.MaxPollRecords)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.MaxPollRecords,
    receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Consumer.ReceiveBufferSizeBytes)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.ReceiveBufferSizeBytes,
    requestTimeout          = env.optDuration(EnvKey.Kafka.Consumer.RequestTimeout)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.RequestTimeout,
    sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Consumer.SendBufferSizeBytes)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.SendBufferSizeBytes,
    autoCommitInterval      = env.optDuration(EnvKey.Kafka.Consumer.AutoCommitInterval)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.AutoCommitInterval,
    clientID                = clientID,
    reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffMaxTime)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,
    reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffTime)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.ReconnectBackoffTime,
    retryBackoffTime        = env.optDuration(EnvKey.Kafka.Consumer.RetryBackoffTime)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.RetryBackoffTime,
    pollDuration            = env.optDuration(EnvKey.Kafka.Consumer.PollDuration)
                                 ?: vdi.lib.kafka.KafkaConsumerConfigDefaults.PollDuration

  )
}

