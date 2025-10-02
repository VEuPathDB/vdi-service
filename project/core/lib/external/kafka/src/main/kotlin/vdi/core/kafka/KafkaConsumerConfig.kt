package vdi.core.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.internals.AutoOffsetResetStrategy.StrategyType
import java.util.Properties
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import vdi.core.config.loadAndCacheStackConfig
import vdi.core.config.kafka.KafkaConfig
import vdi.core.config.kafka.KafkaConsumerConfig
import vdi.model.field.HostAddress

data class KafkaConsumerConfig(
  /**
   * `bootstrap.servers`
   */
  val servers: List<HostAddress>,

  /**
   * `fetch.min.bytes`
   */
  val fetchMinBytes: Int,

  /**
   * `group.id`
   */
  val groupID: String,

  /**
   * `heartbeat.interval.ms`
   */
  val heartbeatInterval: Duration,

  /**
   * `session.timeout.ms`
   */
  val sessionTimeout: Duration,

  /**
   * `auto.offset.reset`
   */
  val autoOffsetReset: StrategyType,

  /**
   * `connections.max.idle.ms`
   */
  val connectionsMaxIdle: Duration,

  /**
   * `default.api.timeout.ms`
   */
  val defaultAPITimeout: Duration,

  /**
   * `enable.auto.commit`
   */
  val enableAutoCommit: Boolean,

  /**
   * `fetch.max.bytes`
   */
  val fetchMaxBytes: Int,

  /**
   * `group.instance.id`
   */
  val groupInstanceID: String?,

  /**
   * `max.poll.interval.ms`
   */
  val maxPollInterval: Duration,

  /**
   * `max.poll.records`
   */
  val maxPollRecords: Int,

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
   * `auto.commit.interval.ms`
   */
  val autoCommitInterval: Duration,

  /**
   * `client.id`
   */
  val clientID: String,

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

  val pollDuration: Duration,
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
    fetchMinBytes           = conf?.fetchMinBytes ?: Default.FetchMinBytes,
    groupID                 = conf?.groupId ?: DefaultGroupID,
    heartbeatInterval       = conf?.heartbeatInterval ?: Default.HeartbeatInterval,
    sessionTimeout          = conf?.sessionTimeout ?: Default.SessionTimeout,
    autoOffsetReset         = conf?.autoOffsetReset?.let { StrategyType.valueOf(it.uppercase()) } ?: Default.AutoOffsetReset,
    connectionsMaxIdle      = conf?.connectionsMaxIdle ?: Default.ConnectionsMaxIdle,
    defaultAPITimeout       = conf?.defaultApiTimeout ?: Default.DefaultAPITimeout,
    enableAutoCommit        = conf?.enableAutoCommit ?: Default.EnableAutoCommit,
    fetchMaxBytes           = conf?.fetchMaxBytes ?: Default.FetchMaxBytes,
    groupInstanceID         = conf?.groupInstanceId ?: Default.GroupInstanceID,
    maxPollInterval         = conf?.maxPollInterval ?: Default.MaxPollInterval,
    maxPollRecords          = conf?.maxPollRecords ?: Default.MaxPollRecords,
    receiveBufferSize       = conf?.receiveBufferSize ?: Default.ReceiveBufferSizeBytes,
    requestTimeout          = conf?.requestTimeout ?: Default.RequestTimeout,
    sendBufferSize          = conf?.sendBufferSize ?: Default.SendBufferSizeBytes,
    autoCommitInterval      = conf?.autoCommitInterval ?: Default.AutoCommitInterval,
    reconnectBackoffMaxTime = conf?.reconnectBackoffMaxTime ?: Default.ReconnectBackoffMaxTime,
    reconnectBackoffTime    = conf?.reconnectBackoffTime ?: Default.ReconnectBackoffTime,
    retryBackoffTime        = conf?.retryBackoffTime ?: Default.RetryBackoffTime,
    pollDuration            = conf?.pollDuration ?: Default.PollDuration,
  )

  fun toProperties() = Properties()
    .apply {
      setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers.joinToString(",") { it.toString() })
      setProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientID)
      setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID)
      setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes.toString())
      setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset.toString())
      setProperty(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, connectionsMaxIdle.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, defaultAPITimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit.toString())
      setProperty(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes.toString())
      groupInstanceID?.also { setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, it) }
      setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords.toString())
      setProperty(ConsumerConfig.RECEIVE_BUFFER_CONFIG, receiveBufferSize.toString())
      setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.SEND_BUFFER_CONFIG, sendBufferSize.toString())
      setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, reconnectBackoffMaxTime.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoffTime.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoffTime.inWholeMilliseconds.toString())
    }

  private companion object {
    inline val DefaultGroupID get() = "vdi-kafka-listeners"
  }

  private object Default {
    inline val FetchMinBytes
      get() = 1

    inline val HeartbeatInterval
      get() = 3.seconds

    inline val SessionTimeout
      get() = 45.seconds

    inline val AutoOffsetReset
      get() = StrategyType.EARLIEST

    inline val ConnectionsMaxIdle
      get() = 9.minutes

    inline val DefaultAPITimeout
      get() = 1.minutes

    inline val EnableAutoCommit
      get() = true

    inline val FetchMaxBytes
      get() = 52428800

    inline val GroupInstanceID
      get() = null as String?

    inline val MaxPollInterval
      get() = 5.minutes

    inline val MaxPollRecords
      get() = 500

    inline val ReceiveBufferSizeBytes
      get() = 65536

    inline val RequestTimeout
      get() = 30.seconds

    inline val SendBufferSizeBytes
      get() = 131072

    inline val AutoCommitInterval
      get() = 5.seconds

    inline val ReconnectBackoffMaxTime
      get() = 1.seconds

    inline val ReconnectBackoffTime
      get() = 50.milliseconds

    inline val RetryBackoffTime
      get() = 100.milliseconds

    inline val PollDuration
      get() = 2.seconds
  }
}

