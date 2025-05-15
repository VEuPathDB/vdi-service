package vdi.lib.config.vdi

data class RabbitQueueConfig(
  val name: String,
  val autoDelete: Boolean?,
  val durable: Boolean?,
  val exclusive: Boolean?,
  val arguments: Map<String, String>?,
)
