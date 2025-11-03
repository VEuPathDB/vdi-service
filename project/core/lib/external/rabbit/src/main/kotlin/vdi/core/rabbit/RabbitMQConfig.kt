package vdi.core.rabbit

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import vdi.core.config.rabbit.RabbitConfig

data class RabbitMQConfig(
  val connection: ConnectionConfig,
  val exchange: ExchangeConfig,
  val queue: QueueConfig,
  val routing: RoutingConfig,
  val messagePollingInterval: Duration = 1.seconds
) {
  constructor(conf: RabbitConfig): this(
    connection = ConnectionConfig(conf.connection),
    exchange = ExchangeConfig(conf.exchange),
    queue = QueueConfig(conf.queue),
    routing = RoutingConfig(conf.routing),
    messagePollingInterval = conf.pollInterval ?: DefaultPollingInterval,
  )

  companion object {
    inline val DefaultPollingInterval: Duration
      get() = 1.seconds
  }
}
