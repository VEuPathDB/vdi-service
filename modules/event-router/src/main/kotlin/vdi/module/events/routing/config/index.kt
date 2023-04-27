package vdi.module.events.routing.config

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.common.util.HostAddress
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterConfig
import org.veupathdb.vdi.lib.rabbit.RabbitMQConfig
import kotlin.time.Duration.Companion.seconds

/**
 * Loads the [EventRouterConfig] from environment variables.
 *
 * @return The loaded `EventRouterConfig` instance.
 */
internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

/**
 * Loads the [EventRouterConfig] from the given map of environment variables.
 *
 * @param env Map of environment variables from which the config should be
 * loaded.
 *
 * @return The loaded `EventRouterConfig` instance.
 */
internal fun loadConfigFromEnvironment(env: Environment) =
  EventRouterConfig(
    rabbitConfig = loadRabbitConfigFromEnvironment(env),
    s3Bucket     = env.require(EnvKey.S3.BucketName),
    kafkaConfig  = loadKafkaConfigFromEnvironment(env)
  )

internal fun loadRabbitConfigFromEnvironment(env: Environment) =
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
