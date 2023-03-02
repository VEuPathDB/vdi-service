package vdi.module.events.routing.config

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import vdi.components.common.fields.SecretString
import vdi.components.common.util.HostAddress
import vdi.components.kafka.KafkaCompressionType
import vdi.components.kafka.KafkaProducerConfig
import vdi.components.kafka.KafkaProducerConfigDefaults
import vdi.components.rabbit.RabbitMQConfig

internal object EnvKey {
  const val RABBIT_USERNAME        = "GLOBAL_RABBIT_USERNAME"
  const val RABBIT_PASSWORD        = "GLOBAL_RABBIT_PASSWORD"
  const val RABBIT_HOST            = "GLOBAL_RABBIT_HOST"
  const val RABBIT_PORT            = "GLOBAL_RABBIT_PORT"
  const val RABBIT_CONNECTION_NAME = "GLOBAL_RABBIT_CONNECTION_NAME"

  const val RABBIT_EXCHANGE_NAME        = "GLOBAL_RABBIT_VDI_EXCHANGE_NAME"
  const val RABBIT_EXCHANGE_TYPE        = "GLOBAL_RABBIT_VDI_EXCHANGE_TYPE"
  const val RABBIT_EXCHANGE_DURABLE     = "GLOBAL_RABBIT_VDI_EXCHANGE_DURABLE"
  const val RABBIT_EXCHANGE_AUTO_DELETE = "GLOBAL_RABBIT_VDI_EXCHANGE_AUTO_DELETE"
  const val RABBIT_EXCHANGE_ARGUMENTS   = "GLOBAL_RABBIT_VDI_EXCHANGE_ARGUMENTS"

  const val RABBIT_QUEUE_NAME        = "GLOBAL_RABBIT_VDI_QUEUE_NAME"
  const val RABBIT_QUEUE_DURABLE     = "GLOBAL_RABBIT_VDI_QUEUE_DURABLE"
  const val RABBIT_QUEUE_EXCLUSIVE   = "GLOBAL_RABBIT_VDI_QUEUE_EXCLUSIVE"
  const val RABBIT_QUEUE_AUTO_DELETE = "GLOBAL_RABBIT_VDI_QUEUE_AUTO_DELETE"
  const val RABBIT_QUEUE_ARGUMENTS   = "GLOBAL_RABBIT_VDI_QUEUE_ARGUMENTS"

  const val RABBIT_ROUTING_KEY       = "GLOBAL_RABBIT_VDI_ROUTING_KEY"
  const val RABBIT_ROUTING_ARGUMENTS = "GLOBAL_RABBIT_VDI_ROUTING_ARGUMENTS"

  const val RABBIT_POLLING_INTERVAL = "GLOBAL_RABBIT_VDI_POLLING_INTERVAL"

  const val S3_BUCKET_NAME = "S3_BUCKET_NAME"

  const val KAFKA_SERVERS = "KAFKA_SERVERS"

  const val KAFKA_PRODUCER_BUFFER_MEMORY_BYTES = "KAFKA_PRODUCER_BUFFER_MEMORY_BYTES"
  const val KAFKA_PRODUCER_COMPRESSION_TYPE = "KAFKA_PRODUCER_COMPRESSION_TYPE"
  const val KAFKA_PRODUCER_SEND_RETRIES = "KAFKA_PRODUCER_SEND_RETRIES"
  const val KAFKA_PRODUCER_BATCH_SIZE = "KAFKA_PRODUCER_BATCH_SIZE"
  const val KAFKA_PRODUCER_CLIENT_ID = "KAFKA_PRODUCER_CLIENT_ID"
  const val KAFKA_PRODUCER_CONNECTIONS_MAX_IDLE = "KAFKA_PRODUCER_CONNECTIONS_MAX_IDLE"
  const val KAFKA_PRODUCER_DELIVERY_TIMEOUT = "KAFKA_PRODUCER_DELIVERY_TIMEOUT"
  const val KAFKA_PRODUCER_LINGER_TIME = "KAFKA_PRODUCER_LINGER_TIME"
  const val KAFKA_PRODUCER_MAX_BLOCKING_TIMEOUT = "KAFKA_PRODUCER_MAX_BLOCKING_TIMEOUT"
  const val KAFKA_PRODUCER_MAX_REQUEST_SIZE_BYTES = "KAFKA_PRODUCER_MAX_REQUEST_SIZE_BYTES"
  const val KAFKA_PRODUCER_RECEIVE_BUFFER_SIZE_BYTES = "KAFKA_PRODUCER_RECEIVE_BUFFER_SIZE_BYTES"
  const val KAFKA_PRODUCER_REQUEST_TIMEOUT = "KAFKA_PRODUCER_REQUEST_TIMEOUT"
  const val KAFKA_PRODUCER_SEND_BUFFER_SIZE_BYTES = "KAFKA_PRODUCER_SEND_BUFFER_SIZE_BYTES"
  const val KAFKA_PRODUCER_RECONNECT_BACKOFF_MAX_TIME = "KAFKA_PRODUCER_RECONNECT_BACKOFF_MAX_TIME"
  const val KAFKA_PRODUCER_RECONNECT_BACKOFF_TIME = "KAFKA_PRODUCER_RECONNECT_BACKOFF_TIME"
  const val KAFKA_PRODUCER_RETRY_BACKOFF_TIME = "KAFKA_PRODUCER_RETRY_BACKOFF_TIME"

  const val KAFKA_MESSAGE_KEY_IMPORT_TRIGGERS = "KAFKA_MESSAGE_KEY_IMPORT_TRIGGERS"
  const val KAFKA_MESSAGE_KEY_INSTALL_TRIGGERS = "KAFKA_MESSAGE_KEY_INSTALL_TRIGGERS"
  const val KAFKA_MESSAGE_KEY_UPDATE_META_TRIGGERS = "KAFKA_MESSAGE_KEY_UPDATE_META_TRIGGERS"
  const val KAFKA_MESSAGE_KEY_SOFT_DELETE_TRIGGERS = "KAFKA_MESSAGE_KEY_SOFT_DELETE_TRIGGERS"
  const val KAFKA_MESSAGE_KEY_HARD_DELETE_TRIGGERS = "KAFKA_MESSAGE_KEY_HARD_DELETE_TRIGGERS"
  const val KAFKA_MESSAGE_KEY_SHARE_TRIGGERS = "KAFKA_MESSAGE_KEY_SHARE_TRIGGERS"

  const val KAFKA_TOPIC_IMPORT_TRIGGERS = "KAFKA_TOPIC_IMPORT_TRIGGERS"
  const val KAFKA_TOPIC_INSTALL_TRIGGERS = "KAFKA_TOPIC_INSTALL_TRIGGERS"
  const val KAFKA_TOPIC_UPDATE_META_TRIGGERS = "KAFKA_TOPIC_UPDATE_META_TRIGGERS"
  const val KAFKA_TOPIC_SOFT_DELETE_TRIGGERS = "KAFKA_TOPIC_SOFT_DELETE_TRIGGERS"
  const val KAFKA_TOPIC_HARD_DELETE_TRIGGERS = "KAFKA_TOPIC_HARD_DELETE_TRIGGERS"
  const val KAFKA_TOPIC_SHARE_TRIGGERS = "KAFKA_TOPIC_SHARE_TRIGGERS"
}


internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Map<String, String>) =
  EventRouterConfig(
    rabbitConfig = loadRabbitConfigFromEnvironment(env),
    s3Bucket     = env.require(EnvKey.S3_BUCKET_NAME),
    kafkaConfig  = loadKafkaConfigFromEnvironment(env)
  )

internal fun loadRabbitConfigFromEnvironment(env: Map<String, String>) =
  RabbitMQConfig(
    serverAddress          = HostAddress(
      host = env.optional(EnvKey.RABBIT_HOST) ?: "localhost",
      port = env.optional(EnvKey.RABBIT_PORT)?.toUShort() ?: 5672u,
    ),
    serverUsername         = env.require(EnvKey.RABBIT_USERNAME),
    serverPassword         = SecretString(env.require(EnvKey.RABBIT_PASSWORD)),
    serverConnectionName   = env.optional(EnvKey.RABBIT_CONNECTION_NAME),

    exchangeName           = env.require(EnvKey.RABBIT_EXCHANGE_NAME),
    exchangeType           = env.optional(EnvKey.RABBIT_EXCHANGE_TYPE) ?: "direct",
    exchangeDurable        = env.optional(EnvKey.RABBIT_EXCHANGE_DURABLE)?.toBool() ?: true,
    exchangeAutoDelete     = env.optional(EnvKey.RABBIT_EXCHANGE_AUTO_DELETE)?.toBool() ?: false,
    exchangeArguments      = env.optional(EnvKey.RABBIT_EXCHANGE_ARGUMENTS)?.toMap() ?: emptyMap(),

    queueName              = env.require(EnvKey.RABBIT_QUEUE_NAME),
    queueDurable           = env.optional(EnvKey.RABBIT_QUEUE_DURABLE)?.toBool() ?: true,
    queueExclusive         = env.optional(EnvKey.RABBIT_QUEUE_EXCLUSIVE)?.toBool() ?: false,
    queueAutoDelete        = env.optional(EnvKey.RABBIT_QUEUE_AUTO_DELETE)?.toBool() ?: false,
    queueArguments         = env.optional(EnvKey.RABBIT_QUEUE_ARGUMENTS)?.toMap() ?: emptyMap(),

    routingKey             = env.optional(EnvKey.RABBIT_ROUTING_KEY) ?: "",
    routingArgs            = env.optional(EnvKey.RABBIT_ROUTING_ARGUMENTS)?.toMap() ?: emptyMap(),
    messagePollingInterval = env.optional(EnvKey.RABBIT_POLLING_INTERVAL)?.let(Duration::parse) ?: 500.milliseconds
  )


internal fun loadKafkaConfigFromEnvironment(env: Map<String, String>) =
  KafkaConfig(
    producerConfig = KafkaProducerConfig(
      servers                 = env.require(EnvKey.KAFKA_SERVERS)
                                   .toHostAddresses(),
      bufferMemoryBytes       = env.optional(EnvKey.KAFKA_PRODUCER_BUFFER_MEMORY_BYTES)
                                   ?.toLong()
                                   ?: KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,
      compressionType         = env.optional(EnvKey.KAFKA_PRODUCER_COMPRESSION_TYPE)
                                   ?.let(KafkaCompressionType.Companion::fromString)
                                   ?: KafkaProducerConfigDefaults.COMPRESSION_TYPE,
      sendRetries             = env.optional(EnvKey.KAFKA_PRODUCER_SEND_RETRIES)
                                   ?.toInt()
                                   ?: KafkaProducerConfigDefaults.SEND_RETRIES,
      batchSize               = env.optional(EnvKey.KAFKA_PRODUCER_BATCH_SIZE)
                                   ?.toInt()
                                   ?: KafkaProducerConfigDefaults.BATCH_SIZE,
      clientID                = env.require(EnvKey.KAFKA_PRODUCER_CLIENT_ID),
      connectionsMaxIdle      = env.optional(EnvKey.KAFKA_PRODUCER_CONNECTIONS_MAX_IDLE)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.CONNECTIONS_MAX_IDLE,
      deliveryTimeout         = env.optional(EnvKey.KAFKA_PRODUCER_DELIVERY_TIMEOUT)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.DELIVERY_TIMEOUT,
      lingerTime              = env.optional(EnvKey.KAFKA_PRODUCER_LINGER_TIME)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.LINGER_TIME,
      maxBlockingTimeout      = env.optional(EnvKey.KAFKA_PRODUCER_MAX_BLOCKING_TIMEOUT)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.MAX_BLOCKING_TIMEOUT,
      maxRequestSizeBytes     = env.optional(EnvKey.KAFKA_PRODUCER_MAX_REQUEST_SIZE_BYTES)
                                   ?.toInt()
                                   ?: KafkaProducerConfigDefaults.MAX_REQUEST_SIZE_BYTES,
      receiveBufferSizeBytes  = env.optional(EnvKey.KAFKA_PRODUCER_RECEIVE_BUFFER_SIZE_BYTES)
                                   ?.toInt()
                                   ?: KafkaProducerConfigDefaults.RECEIVE_BUFFER_SIZE_BYTES,
      requestTimeout          = env.optional(EnvKey.KAFKA_PRODUCER_REQUEST_TIMEOUT)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.REQUEST_TIMEOUT,
      sendBufferSizeBytes     = env.optional(EnvKey.KAFKA_PRODUCER_SEND_BUFFER_SIZE_BYTES)
                                   ?.toInt()
                                   ?: KafkaProducerConfigDefaults.SEND_BUFFER_SIZE_BYTES,
      reconnectBackoffMaxTime = env.optional(EnvKey.KAFKA_PRODUCER_RECONNECT_BACKOFF_MAX_TIME)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_MAX_TIME,
      reconnectBackoffTime    = env.optional(EnvKey.KAFKA_PRODUCER_RECONNECT_BACKOFF_TIME)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.RECONNECT_BACKOFF_TIME,
      retryBackoffTime        = env.optional(EnvKey.KAFKA_PRODUCER_RETRY_BACKOFF_TIME)
                                   ?.let(Duration.Companion::parse)
                                   ?: KafkaProducerConfigDefaults.RETRY_BACKOFF_TIME
    ),

    importTriggerMessageKey     = env.optional(EnvKey.KAFKA_MESSAGE_KEY_IMPORT_TRIGGERS)
                                     ?: KafkaConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
    importTriggerTopic          = env.optional(EnvKey.KAFKA_TOPIC_IMPORT_TRIGGERS)
                                     ?: KafkaConfigDefaults.IMPORT_TRIGGER_TOPIC,
    installTriggerMessageKey    = env.optional(EnvKey.KAFKA_MESSAGE_KEY_INSTALL_TRIGGERS)
                                     ?: KafkaConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
    installTriggerTopic         = env.optional(EnvKey.KAFKA_TOPIC_INSTALL_TRIGGERS)
                                     ?: KafkaConfigDefaults.INSTALL_TRIGGER_TOPIC,
    updateMetaTriggerMessageKey = env.optional(EnvKey.KAFKA_MESSAGE_KEY_UPDATE_META_TRIGGERS)
                                     ?: KafkaConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
    updateMetaTriggerTopic      = env.optional(EnvKey.KAFKA_TOPIC_UPDATE_META_TRIGGERS)
                                     ?: KafkaConfigDefaults.UPDATE_META_TRIGGER_TOPIC,
    softDeleteTriggerMessageKey = env.optional(EnvKey.KAFKA_MESSAGE_KEY_SOFT_DELETE_TRIGGERS)
                                     ?: KafkaConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
    softDeleteTriggerTopic      = env.optional(EnvKey.KAFKA_TOPIC_SOFT_DELETE_TRIGGERS)
                                     ?: KafkaConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,
    hardDeleteTriggerMessageKey = env.optional(EnvKey.KAFKA_MESSAGE_KEY_HARD_DELETE_TRIGGERS)
                                     ?: KafkaConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
    hardDeleteTriggerTopic      = env.optional(EnvKey.KAFKA_TOPIC_HARD_DELETE_TRIGGERS)
                                     ?: KafkaConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,
    shareTriggerMessageKey      = env.optional(EnvKey.KAFKA_MESSAGE_KEY_SHARE_TRIGGERS)
                                     ?: KafkaConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
    shareTriggerTopic           = env.optional(EnvKey.KAFKA_TOPIC_SHARE_TRIGGERS)
                                     ?: KafkaConfigDefaults.SHARE_TRIGGER_TOPIC,
  )



private fun String.toBool() =
  when (this.lowercase()) {
    "true", "yes", "on", "1" -> true
    else                     -> false
  }

private fun String.toPairSequence() = splitToSequence(',')
  .map { it.toKeyValue() }

private fun String.toMap() = toPairSequence()
  .toMap()

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

private fun Map<String, String>.optional(key: String): String? {
  val value = get(key)

  return if (value.isNullOrBlank())
    null
  else
    value
}

private fun Map<String, String>.require(key: String): String {
  val value = get(key)

  return if (value.isNullOrBlank())
    throw IllegalStateException("Missing required environment variable: $key")
  else
    value
}