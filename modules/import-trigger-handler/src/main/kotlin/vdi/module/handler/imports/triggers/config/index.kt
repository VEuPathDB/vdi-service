package vdi.module.handler.imports.triggers.config

import vdi.components.common.env.*
import vdi.components.common.util.HostAddress
import vdi.components.kafka.*

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Map<String, String>) =
  ImportTriggerHandlerConfig(
    kafkaConfig = loadKafkaConfigFromEnvironment(env),
    s3Bucket    = env.require(EnvKey.S3.BucketName)
  )

internal fun loadKafkaConfigFromEnvironment(env: Map<String, String>) =
  KafkaConfig(
    consumerConfig = KafkaConsumerConfig(
      servers                 = env.require(EnvKey.Kafka.Servers)
                                   .toHostAddresses(),
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
      clientID                = env.require(EnvKey.Kafka.Consumer.ClientID),
      reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffMaxTime)
                                   ?: KafkaConsumerConfigDefaults.ReconnectBackoffMaxTime,
      reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Consumer.ReconnectBackoffTime)
                                   ?: KafkaConsumerConfigDefaults.ReconnectBackoffTime,
      retryBackoffTime        = env.optDuration(EnvKey.Kafka.Consumer.RetryBackoffTime)
                                   ?: KafkaConsumerConfigDefaults.RetryBackoffTime,
      pollDuration            = env.optDuration(EnvKey.Kafka.Consumer.PollDuration)
                                   ?: KafkaConsumerConfigDefaults.PollDuration
    ),

    producerConfig = KafkaProducerConfig(
      servers                 = env.require(EnvKey.Kafka.Servers)
                                   .toHostAddresses(),
      bufferMemoryBytes       = env.optLong(EnvKey.Kafka.Producer.BufferMemoryBytes)
                                   ?: KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,
      compressionType         = env.optional(EnvKey.Kafka.Producer.CompressionType)
                                   ?.let(KafkaCompressionType::fromString)
                                   ?: KafkaProducerConfigDefaults.COMPRESSION_TYPE,
      sendRetries             = env.optInt(EnvKey.Kafka.Producer.SendRetries)
                                   ?: KafkaProducerConfigDefaults.SEND_RETRIES,
      batchSize               = env.optInt(EnvKey.Kafka.Producer.BatchSize)
                                   ?: KafkaProducerConfigDefaults.BATCH_SIZE,
      clientID                = env.require(EnvKey.Kafka.Producer.ClientID),
      connectionsMaxIdle      = env.optDuration(EnvKey.Kafka.Producer.ConnectionsMaxIdle)
                                   ?: KafkaProducerConfigDefaults.CONNECTIONS_MAX_IDLE,
      deliveryTimeout         = env.optDuration(EnvKey.Kafka.Producer.DeliveryTimeout)
                                   ?: KafkaProducerConfigDefaults.DELIVERY_TIMEOUT,
      lingerTime              = env.optDuration(EnvKey.Kafka.Producer.LingerTime)
                                   ?: KafkaProducerConfigDefaults.LINGER_TIME,
      maxBlockingTimeout      = env.optDuration(EnvKey.Kafka.Producer.MaxBlockingTimeout)
                                   ?: KafkaProducerConfigDefaults.MAX_BLOCKING_TIMEOUT,
      maxRequestSizeBytes     = env.optInt(EnvKey.Kafka.Producer.MaxRequestSizeBytes)
                                   ?: KafkaProducerConfigDefaults.MAX_REQUEST_SIZE_BYTES,
      receiveBufferSizeBytes  = env.optInt(EnvKey.Kafka.Producer.ReceiveBufferSizeBytes)
                                   ?: KafkaProducerConfigDefaults.RECEIVE_BUFFER_SIZE_BYTES,
      requestTimeout          = env.optDuration(EnvKey.Kafka.Producer.RequestTimeout)
                                   ?: KafkaProducerConfigDefaults.REQUEST_TIMEOUT,
      sendBufferSizeBytes     = env.optInt(EnvKey.Kafka.Producer.SendBufferSizeBytes)
                                   ?: KafkaProducerConfigDefaults.SEND_BUFFER_SIZE_BYTES,
      reconnectBackoffMaxTime = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffMaxTime)
                                   ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_MAX_TIME,
      reconnectBackoffTime    = env.optDuration(EnvKey.Kafka.Producer.ReconnectBackoffTime)
                                   ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_TIME,
      retryBackoffTime        = env.optDuration(EnvKey.Kafka.Producer.RetryBackoffTime)
                                   ?: KafkaProducerConfigDefaults.RETRY_BACKOFF_TIME
    ),

    importTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerMessageKey,
    importTriggerTopic      = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
                                 ?: KafkaConfigDefaults.ImportTriggerTopic,
    importResultMessageKey  = env.optional(EnvKey.Kafka.MessageKey.ImportResults)
                                 ?: KafkaConfigDefaults.ImportResultMessageKey,
    importResultTopic       = env.optional(EnvKey.Kafka.Topic.ImportResults)
                                 ?: KafkaConfigDefaults.ImportResultTopic
  )


private fun String.toPairSequence() = splitToSequence(',')
  .map { it.toKeyValue() }

private fun String.toHostAddresses() = toPairSequence()
  .map { HostAddress(it.first, it.second.toUShort()) }
  .toList()
  .toTypedArray()

private fun String.toKeyValue(): Pair<String, String> {
  val index = indexOf(':')

  if (index < 0)
    throw IllegalStateException("malformed map in environment variable, no key/value separator character was found")
  if (index < 1)
    throw IllegalStateException("malformed map in environment variable, entry with empty key was found")

  return substring(0, index) to substring(index+1)
}
