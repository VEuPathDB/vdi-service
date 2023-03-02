package vdi.components.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.clients.consumer.KafkaConsumer as KConsumer
import org.apache.kafka.clients.producer.KafkaProducer as KProducer
import java.util.Properties

fun KafkaConsumer(topic: String, config: KafkaConsumerConfig): KafkaConsumer {
  val props = Properties()
    .apply {
      setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers.joinToString(",") { it.toString() })
      setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, config.fetchMinBytes.toString())
      setProperty(ConsumerConfig.GROUP_ID_CONFIG, config.groupID)
      setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, config.heartbeatInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, config.sessionTimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.autoOffsetReset.toString())
      setProperty(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, config.connectionsMaxIdle.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, config.defaultAPITimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.enableAutoCommit.toString())
      setProperty(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, config.fetchMaxBytes.toString())
      config.groupInstanceID?.also { setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, it) }
      setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, config.maxPollInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.maxPollRecords.toString())
      setProperty(ConsumerConfig.RECEIVE_BUFFER_CONFIG, config.receiveBufferSizeBytes.toString())
      setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.requestTimeout.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.SEND_BUFFER_CONFIG, config.sendBufferSizeBytes.toString())
      setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, config.autoCommitInterval.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.CLIENT_ID_CONFIG, config.clientID)
      setProperty(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, config.reconnectBackoffMaxTime.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, config.reconnectBackoffTime.inWholeMilliseconds.toString())
      setProperty(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, config.retryBackoffTime.inWholeMilliseconds.toString())
    }

  return KafkaConsumerImpl(
    topic,
    config.pollDuration,
    StringDeserializer()
      .let { KConsumer(props, it, it).apply { subscribe(listOf(topic)) } }
  )
}

fun KafkaProducer(config: KafkaProducerConfig): KafkaProducer {
  val props = Properties()
    .apply {
      setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.servers.joinToString(",") { it.toString() })
      setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, config.bufferMemoryBytes.toString())
      setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, config.compressionType.toString())
      setProperty(ProducerConfig.RETRIES_CONFIG, config.sendRetries.toString())
      setProperty(ProducerConfig.BATCH_SIZE_CONFIG, config.batchSize.toString())
      setProperty(ProducerConfig.CLIENT_ID_CONFIG, config.clientID)
      setProperty(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, config.connectionsMaxIdle.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, config.deliveryTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.LINGER_MS_CONFIG, config.lingerTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, config.maxBlockingTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, config.maxRequestSizeBytes.toString())
      setProperty(ProducerConfig.RECEIVE_BUFFER_CONFIG, config.receiveBufferSizeBytes.toString())
      setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, config.requestTimeout.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.SEND_BUFFER_CONFIG, config.sendBufferSizeBytes.toString())
      setProperty(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, config.reconnectBackoffMaxTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, config.reconnectBackoffTime.inWholeMilliseconds.toString())
      setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, config.retryBackoffTime.inWholeMilliseconds.toString())
    }

  return KafkaProducerImpl(StringSerializer().let { KProducer(props, it, it) })
}