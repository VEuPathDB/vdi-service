package vdi.module.events.routing.config

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.common.util.HostAddress
import kotlin.time.Duration.Companion.milliseconds
import vdi.components.kafka.KafkaCompressionType
import vdi.components.kafka.KafkaProducerConfig
import vdi.components.kafka.KafkaProducerConfigDefaults
import vdi.components.kafka.router.KafkaRouterConfigDefaults
import vdi.components.rabbit.RabbitMQConfig

internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Environment) =
  EventRouterConfig(
    rabbitConfig = loadRabbitConfigFromEnvironment(env),
    s3Bucket     = env.require(EnvKey.S3.BucketName),
    kafkaConfig  = loadKafkaConfigFromEnvironment(env)
  )

internal fun loadRabbitConfigFromEnvironment(env: Map<String, String>) =
  RabbitMQConfig(
    serverAddress          = HostAddress(
      host = env.optional(EnvKey.Rabbit.Host) ?: "localhost",
      port = env.optional(EnvKey.Rabbit.Port)?.toUShort() ?: 5672u,
    ),
    serverUsername         = env.require(EnvKey.Rabbit.Username),
    serverPassword         = SecretString(env.require(EnvKey.Rabbit.Password)),
    serverConnectionName   = env.optional(EnvKey.Rabbit.ConnectionName),

    exchangeName           = env.require(EnvKey.Rabbit.Exchange.Name),
    exchangeType           = env.optional(EnvKey.Rabbit.Exchange.Type) ?: "direct",
    exchangeDurable        = env.optBool(EnvKey.Rabbit.Exchange.Durable) ?: true,
    exchangeAutoDelete     = env.optBool(EnvKey.Rabbit.Exchange.AutoDelete) ?: false,
    exchangeArguments      = env.optMap(EnvKey.Rabbit.Exchange.Arguments) ?: emptyMap(),

    queueName              = env.require(EnvKey.Rabbit.Queue.Name),
    queueDurable           = env.optBool(EnvKey.Rabbit.Queue.Durable) ?: true,
    queueExclusive         = env.optBool(EnvKey.Rabbit.Queue.Exclusive) ?: false,
    queueAutoDelete        = env.optBool(EnvKey.Rabbit.Queue.AutoDelete) ?: false,
    queueArguments         = env.optMap(EnvKey.Rabbit.Queue.Arguments) ?: emptyMap(),

    routingKey             = env.optional(EnvKey.Rabbit.Routing.Key) ?: "",
    routingArgs            = env.optMap(EnvKey.Rabbit.Routing.Arguments) ?: emptyMap(),

    messagePollingInterval = env.optDuration(EnvKey.Rabbit.PollingInterval) ?: 500.milliseconds
  )

internal fun loadKafkaConfigFromEnvironment(env: Map<String, String>) =
  KafkaConfig(
    producerConfig = KafkaProducerConfig(
      servers                 = env.require(EnvKey.Kafka.Servers)
                                   .toHostAddresses(),
      bufferMemoryBytes       = env.optional(EnvKey.Kafka.Producer.BufferMemoryBytes)
                                   ?.toLong()
                                   ?: KafkaProducerConfigDefaults.BUFFER_MEMORY_BYTES,
      compressionType         = env.optional(EnvKey.Kafka.Producer.CompressionType)
                                   ?.let(KafkaCompressionType.Companion::fromString)
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

    importTriggerMessageKey     = env.optional(EnvKey.Kafka.MessageKey.ImportTriggers)
                                     ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_MESSAGE_KEY,
    importTriggerTopic          = env.optional(EnvKey.Kafka.Topic.ImportTriggers)
                                     ?: KafkaRouterConfigDefaults.IMPORT_TRIGGER_TOPIC,
    installTriggerMessageKey    = env.optional(EnvKey.Kafka.MessageKey.InstallTriggers)
                                     ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_MESSAGE_KEY,
    installTriggerTopic         = env.optional(EnvKey.Kafka.Topic.InstallTriggers)
                                     ?: KafkaRouterConfigDefaults.INSTALL_TRIGGER_TOPIC,
    updateMetaTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.UpdateMetaTriggers)
                                     ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_MESSAGE_KEY,
    updateMetaTriggerTopic      = env.optional(EnvKey.Kafka.Topic.UpdateMetaTriggers)
                                     ?: KafkaRouterConfigDefaults.UPDATE_META_TRIGGER_TOPIC,
    softDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.SoftDeleteTriggers)
                                     ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_MESSAGE_KEY,
    softDeleteTriggerTopic      = env.optional(EnvKey.Kafka.Topic.SoftDeleteTriggers)
                                     ?: KafkaRouterConfigDefaults.SOFT_DELETE_TRIGGER_TOPIC,
    hardDeleteTriggerMessageKey = env.optional(EnvKey.Kafka.MessageKey.HardDeleteTriggers)
                                     ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_MESSAGE_KEY,
    hardDeleteTriggerTopic      = env.optional(EnvKey.Kafka.Topic.HardDeleteTriggers)
                                     ?: KafkaRouterConfigDefaults.HARD_DELETE_TRIGGER_TOPIC,
    shareTriggerMessageKey      = env.optional(EnvKey.Kafka.MessageKey.ShareTriggers)
                                     ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_MESSAGE_KEY,
    shareTriggerTopic           = env.optional(EnvKey.Kafka.Topic.ShareTriggers)
                                     ?: KafkaRouterConfigDefaults.SHARE_TRIGGER_TOPIC,
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
