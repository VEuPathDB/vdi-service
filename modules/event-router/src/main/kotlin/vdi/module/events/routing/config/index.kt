package vdi.module.events.routing.config

import vdi.components.common.SecretString

// region Global Rabbit Config Keys

// Server Connection Settings
private const val ENV_KEY_RABBIT_USERNAME         = "GLOBAL_RABBIT_USERNAME"
private const val ENV_KEY_RABBIT_PASSWORD         = "GLOBAL_RABBIT_PASSWORD"
private const val ENV_KEY_RABBIT_HOST             = "GLOBAL_RABBIT_HOST"
private const val ENV_KEY_RABBIT_PORT             = "GLOBAL_RABBIT_PORT"
private const val ENV_KEY_RABBIT_CONNECTION_NAME  = "GLOBAL_RABBIT_CONNECTION_NAME"

// Exchange Settings
private const val ENV_KEY_RABBIT_EXCHANGE_NAME        = "GLOBAL_RABBIT_VDI_EXCHANGE_NAME"
private const val ENV_KEY_RABBIT_EXCHANGE_TYPE        = "GLOBAL_RABBIT_VDI_EXCHANGE_TYPE"
private const val ENV_KEY_RABBIT_EXCHANGE_DURABLE     = "GLOBAL_RABBIT_VDI_EXCHANGE_DURABLE"
private const val ENV_KEY_RABBIT_EXCHANGE_AUTO_DELETE = "GLOBAL_RABBIT_VDI_EXCHANGE_AUTO_DELETE"
private const val ENV_KEY_RABBIT_EXCHANGE_ARGUMENTS   = "GLOBAL_RABBIT_VDI_EXCHANGE_ARGUMENTS"

// Queue Settings
private const val ENV_KEY_RABBIT_QUEUE_NAME        = "GLOBAL_RABBIT_VDI_QUEUE_NAME"
private const val ENV_KEY_RABBIT_QUEUE_DURABLE     = "GLOBAL_RABBIT_VDI_QUEUE_DURABLE"
private const val ENV_KEY_RABBIT_QUEUE_EXCLUSIVE   = "GLOBAL_RABBIT_VDI_QUEUE_EXCLUSIVE"
private const val ENV_KEY_RABBIT_QUEUE_AUTO_DELETE = "GLOBAL_RABBIT_VDI_QUEUE_AUTO_DELETE"
private const val ENV_KEY_RABBIT_QUEUE_ARGUMENTS   = "GLOBAL_RABBIT_VDI_QUEUE_ARGUMENTS"

// Routing Settings
private const val ENV_KEY_RABBIT_ROUTING_KEY       = "GLOBAL_RABBIT_VDI_ROUTING_KEY"
private const val ENV_KEY_RABBIT_ROUTING_ARGUMENTS = "GLOBAL_RABBIT_VDI_ROUTING_ARGUMENTS"

// endregion Global Rabbit Config Keys


internal fun loadConfigFromEnvironment() = loadConfigFromEnvironment(System.getenv())

internal fun loadConfigFromEnvironment(env: Map<String, String>) = EventRouterConfig(
  loadRabbitConfigFromEnvironment(env)
)

internal fun loadRabbitConfigFromEnvironment(env: Map<String, String>) = EventRouterRabbitConfig(
  serverHost           = env.optional(ENV_KEY_RABBIT_HOST) ?: "localhost",
  serverPort           = env.optional(ENV_KEY_RABBIT_PORT)?.toUShort() ?: 5672u,
  serverUsername       = env.require(ENV_KEY_RABBIT_USERNAME),
  serverPassword       = SecretString(env.require(ENV_KEY_RABBIT_PASSWORD)),
  serverConnectionName = env.optional(ENV_KEY_RABBIT_CONNECTION_NAME),

  exchangeName         = env.require(ENV_KEY_RABBIT_EXCHANGE_NAME),
  exchangeType         = env.optional(ENV_KEY_RABBIT_EXCHANGE_TYPE) ?: "direct",
  exchangeDurable      = env.optional(ENV_KEY_RABBIT_EXCHANGE_DURABLE)?.toBool() ?: true,
  exchangeAutoDelete   = env.optional(ENV_KEY_RABBIT_EXCHANGE_AUTO_DELETE)?.toBool() ?: false,
  exchangeArguments    = env.optional(ENV_KEY_RABBIT_EXCHANGE_ARGUMENTS)?.toMap() ?: emptyMap(),

  queueName            = env.require(ENV_KEY_RABBIT_QUEUE_NAME),
  queueDurable         = env.optional(ENV_KEY_RABBIT_QUEUE_DURABLE)?.toBool() ?: true,
  queueExclusive       = env.optional(ENV_KEY_RABBIT_QUEUE_EXCLUSIVE)?.toBool() ?: false,
  queueAutoDelete      = env.optional(ENV_KEY_RABBIT_QUEUE_AUTO_DELETE)?.toBool() ?: false,
  queueArguments       = env.optional(ENV_KEY_RABBIT_QUEUE_ARGUMENTS)?.toMap() ?: emptyMap(),

  routingKey           = env.optional(ENV_KEY_RABBIT_ROUTING_KEY) ?: "",
  routingArgs          = env.optional(ENV_KEY_RABBIT_ROUTING_ARGUMENTS)?.toMap() ?: emptyMap()
)

private fun String.toBool() =
  when (this.lowercase()) {
    "true", "yes", "on", "1" -> true
    else                     -> false
  }

private fun String.toMap() = this.splitToSequence(',')
  .map { it.toKeyValue() }
  .toMap()

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