package vdi.lib.kafka

import kotlin.time.Duration
import vdi.config.loadAndCacheStackConfig
import vdi.config.raw.kafka.KafkaConfig
import vdi.config.raw.kafka.KafkaConsumerConfig
import vdi.model.field.HostAddress

data class KafkaConsumerConfig(
  // bootstrap.servers
  val servers: List<HostAddress>,

  // fetch.min.bytes
  val fetchMinBytes: Int?,

  // group.id
  val groupID: String,

  // heartbeat.interval.ms
  val heartbeatInterval: Duration?,

  // session.timeout.ms
  val sessionTimeout: Duration?,

  // auto.offset.reset
  val autoOffsetReset: KafkaOffsetType?,

  // connections.max.idle.ms
  val connectionsMaxIdle: Duration?,

  // default.api.timeout.ms
  val defaultAPITimeout: Duration?,

  // enable.auto.commit
  val enableAutoCommit: Boolean?,

  // fetch.max.bytes
  val fetchMaxBytes: Int?,

  // group.instance.id
  val groupInstanceID: String?,

  // max.poll.interval.ms
  val maxPollInterval: Duration?,

  // max.poll.records
  val maxPollRecords: Int?,

  // receive.buffer.bytes
  val receiveBufferSize: Int?,

  // request.timeout.ms
  val requestTimeout: Duration?,

  // send.buffer.bytes
  val sendBufferSize: Int?,

  // auto.commit.interval.ms
  val autoCommitInterval: Duration?,

  // client.id
  val clientID: String,

  // reconnect.backoff.max.ms
  val reconnectBackoffMaxTime: Duration?,

  // reconnect.backoff.ms
  val reconnectBackoffTime: Duration?,

  // retry.backoff.ms
  val retryBackoffTime: Duration?,

  val pollDuration: Duration?,
) {
  constructor(clientID: String): this(clientID, loadAndCacheStackConfig().vdi.kafka)

  constructor(clientID: String, conf: KafkaConfig) : this(
    clientID,
    conf.servers.map { it.toHostAddress(DefaultServerPort) },
    conf.consumers,
  )

  constructor(clientID: String, servers: List<HostAddress>, conf: KafkaConsumerConfig?) : this(
    servers                 = servers,
    clientID                = clientID,
    fetchMinBytes           = conf?.fetchMinBytes,
    groupID                 = conf?.groupId ?: DefaultGroupID,
    heartbeatInterval       = conf?.heartbeatInterval,
    sessionTimeout          = conf?.sessionTimeout,
    autoOffsetReset         = conf?.autoOffsetReset?.let(KafkaOffsetType::fromString),
    connectionsMaxIdle      = conf?.connectionsMaxIdle,
    defaultAPITimeout       = conf?.defaultApiTimeout,
    enableAutoCommit        = conf?.enableAutoCommit,
    fetchMaxBytes           = conf?.fetchMaxBytes,
    groupInstanceID         = conf?.groupInstanceId,
    maxPollInterval         = conf?.maxPollInterval,
    maxPollRecords          = conf?.maxPollRecords,
    receiveBufferSize       = conf?.receiveBufferSize,
    requestTimeout          = conf?.requestTimeout,
    sendBufferSize          = conf?.sendBufferSize,
    autoCommitInterval      = conf?.autoCommitInterval,
    reconnectBackoffMaxTime = conf?.reconnectBackoffMaxTime,
    reconnectBackoffTime    = conf?.reconnectBackoffTime,
    retryBackoffTime        = conf?.retryBackoffTime,
    pollDuration            = conf?.pollDuration,
  )

  private companion object {
    inline val DefaultGroupID get() = "vdi-kafka-listeners"
  }
}

