package vdi.core.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import kotlin.time.Duration.Companion.seconds
import vdi.core.health.RemoteDependencies
import vdi.model.field.HostAddress
import org.apache.kafka.clients.consumer.KafkaConsumer as KConsumer
import org.apache.kafka.clients.producer.KafkaProducer as KProducer

private val knownServers = HashSet<HostAddress>(1)
private fun init(servers: Iterable<HostAddress>) {
  synchronized(knownServers) {
    for (server in servers) {
      if (server !in knownServers) {
        knownServers.add(server)
        RemoteDependencies.register("Kafka $server", server.host, server.port)
      }
    }
  }
}

fun KafkaConsumer(topic: MessageTopic, config: KafkaConsumerConfig): KafkaConsumer {
  init(config.servers)
  val props = Properties()
    .apply {
      setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers.joinToString(",") { it.toString() })
      setProperty(ConsumerConfig.CLIENT_ID_CONFIG, config.clientID)
      setProperty(ConsumerConfig.GROUP_ID_CONFIG, config.groupID)

      // Optional
      config.fetchMinBytes?.also { setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, it.toString()) }
      config.heartbeatInterval?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, it.toString()) }
      config.sessionTimeout?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, it.toString()) }
      config.autoOffsetReset?.also { setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, it.toString()) }
      config.connectionsMaxIdle?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, it.toString()) }
      config.defaultAPITimeout?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, it.toString()) }
      config.enableAutoCommit?.also { setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, it.toString()) }
      config.fetchMaxBytes?.also { setProperty(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, it.toString()) }
      config.groupInstanceID?.also { setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, it) }
      config.maxPollInterval?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, it.toString()) }
      config.maxPollRecords?.also { setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, it.toString()) }
      config.receiveBufferSize?.also { setProperty(ConsumerConfig.RECEIVE_BUFFER_CONFIG, it.toString()) }
      config.requestTimeout?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, it.toString()) }
      config.sendBufferSize?.also { setProperty(ConsumerConfig.SEND_BUFFER_CONFIG, it.toString()) }
      config.autoCommitInterval?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, it.toString()) }
      config.reconnectBackoffMaxTime?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, it.toString()) }
      config.reconnectBackoffTime?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, it.toString()) }
      config.retryBackoffTime?.inWholeMilliseconds?.also { setProperty(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, it.toString()) }
    }

  return KafkaConsumerImpl(
    topic,
    config.pollDuration ?: 2.seconds,
    StringDeserializer()
      .let { KConsumer(props, it, it).apply { subscribe(listOf(topic.value)) } }
  )
}

fun KafkaProducer(config: KafkaProducerConfig): KafkaProducer {
  init(config.servers)
  return KafkaProducerImpl(StringSerializer().let { KProducer(config.toProperties(), it, it) })
}
