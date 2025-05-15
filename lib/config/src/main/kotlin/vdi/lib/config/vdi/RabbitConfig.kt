package vdi.lib.config.vdi

import kotlin.time.Duration

data class RabbitConfig(
  val pollInterval: Duration?,
  val connection: RabbitConnectionConfig,
  val exchange: RabbitExchangeConfig,
  val queue: RabbitQueueConfig,
  val routing: RabbitRoutingConfig?,
)
