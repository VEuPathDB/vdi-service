package vdi.component.rabbit

import org.veupathdb.vdi.lib.common.env.*
import org.veupathdb.vdi.lib.common.field.SecretString
import org.veupathdb.vdi.lib.common.util.HostAddress
import vdi.component.env.EnvKey
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class RabbitMQConfig(
  val serverAddress: HostAddress,
  val serverUsername: String,
  val serverPassword: SecretString,
  val serverConnectionName: String? = null,
  val serverUseTLS: Boolean,

  val exchangeName: String,
  val exchangeType: String = "direct",
  val exchangeDurable: Boolean = true,
  val exchangeAutoDelete: Boolean = false,
  val exchangeArguments: Map<String, Any> = mapOf(),

  val queueName: String,
  val queueDurable: Boolean = true,
  val queueExclusive: Boolean = false,
  val queueAutoDelete: Boolean = false,
  val queueArguments: Map<String, Any> = mapOf(),

  val routingKey: String = "",
  val routingArguments: Map<String, Any> = mapOf(),

  val messagePollingInterval: Duration = 1.seconds
) {
  constructor(env: Environment) : this(
    serverAddress = HostAddress(
      host = env.require(EnvKey.Rabbit.Host),
      port = env.optUShort(EnvKey.Rabbit.Port) ?: RabbitMQConfigDefaults.Port
    ),
    serverUsername       = env.require(EnvKey.Rabbit.Username),
    serverPassword       = SecretString(env.require(EnvKey.Rabbit.Password)),
    serverConnectionName = env.optional(EnvKey.Rabbit.ConnectionName),
    serverUseTLS         = env.optBool(EnvKey.Rabbit.UseTLS) ?: RabbitMQConfigDefaults.UseTLS,

    exchangeName       = env.require(EnvKey.Rabbit.Exchange.Name),
    exchangeType       = env.optional(EnvKey.Rabbit.Exchange.Type) ?: RabbitMQConfigDefaults.ExchangeType,
    exchangeDurable    = env.optBool(EnvKey.Rabbit.Exchange.Durable) ?: RabbitMQConfigDefaults.ExchangeDurable,
    exchangeAutoDelete = env.optBool(EnvKey.Rabbit.Exchange.AutoDelete) ?: RabbitMQConfigDefaults.ExchangeAutoDelete,
    exchangeArguments  = env.optMap(EnvKey.Rabbit.Exchange.Arguments) ?: RabbitMQConfigDefaults.ExchangeArguments,

    queueName       = env.require(EnvKey.Rabbit.Queue.Name),
    queueDurable    = env.optBool(EnvKey.Rabbit.Queue.Durable) ?: RabbitMQConfigDefaults.QueueDurable,
    queueExclusive  = env.optBool(EnvKey.Rabbit.Queue.Exclusive) ?: RabbitMQConfigDefaults.QueueExclusive,
    queueAutoDelete = env.optBool(EnvKey.Rabbit.Queue.AutoDelete) ?: RabbitMQConfigDefaults.QueueAutoDelete,
    queueArguments  = env.optMap(EnvKey.Rabbit.Queue.Arguments) ?: RabbitMQConfigDefaults.QueueArguments,

    routingKey       = env.optional(EnvKey.Rabbit.Routing.Key) ?: RabbitMQConfigDefaults.RoutingKey,
    routingArguments = env.optMap(EnvKey.Rabbit.Routing.Arguments) ?: RabbitMQConfigDefaults.RoutingArguments,

    messagePollingInterval = env.optDuration(EnvKey.Rabbit.PollingInterval) ?: RabbitMQConfigDefaults.MessagePollingInterval
  )
}
