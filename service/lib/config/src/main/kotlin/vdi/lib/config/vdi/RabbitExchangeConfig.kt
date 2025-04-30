package vdi.lib.config.vdi

data class RabbitExchangeConfig(
  val name: String,
  val type: String?,
  val autoDelete: Boolean?,
  val durable: Boolean?,
  val arguments: Map<String, String>?,
)
