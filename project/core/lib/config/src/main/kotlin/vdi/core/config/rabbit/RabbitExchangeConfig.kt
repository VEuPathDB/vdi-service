package vdi.core.config.rabbit

data class RabbitExchangeConfig(
  val name: String,
  val type: String?,
  val autoDelete: Boolean?,
  val durable: Boolean?,
  val arguments: Map<String, String>?,
)
