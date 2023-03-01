package vdi.module.events.routing.config

import vdi.components.common.SecretString

data class EventRouterRabbitConfig(
  val serverHost: String = "localhost",
  val serverPort: UShort = 5672u,
  val serverUsername: String,
  val serverPassword: SecretString,
  val serverConnectionName: String? = null,

  val exchangeName:    String,
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
  val routingArgs: Map<String, Any> = mapOf(),
)