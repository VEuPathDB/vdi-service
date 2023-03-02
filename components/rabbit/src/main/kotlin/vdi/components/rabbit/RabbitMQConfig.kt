package vdi.components.rabbit

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import vdi.components.common.fields.SecretString
import vdi.components.common.util.HostAddress

data class RabbitMQConfig(
  val serverAddress: HostAddress = HostAddress("localhost", 5672u),
  val serverUsername: String,
  val serverPassword: SecretString,
  val serverConnectionName: String? = null,

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
  val routingArgs: Map<String, Any> = mapOf(),

  val messagePollingInterval: Duration = 500.milliseconds
)
