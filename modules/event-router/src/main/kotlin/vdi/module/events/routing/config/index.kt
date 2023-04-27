package vdi.module.events.routing.config

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.common.util.HostAddress
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.rabbit.RabbitMQConfig
import kotlin.time.Duration.Companion.seconds

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
      host = env.require(EnvKey.Rabbit.Host),
      port = env.optUShort(EnvKey.Rabbit.Port) ?: 5672u,
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

    messagePollingInterval = env.optDuration(EnvKey.Rabbit.PollingInterval) ?: 1.seconds
  )

internal fun loadKafkaConfigFromEnvironment(env: Environment) = KafkaRouterConfig(env)

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
