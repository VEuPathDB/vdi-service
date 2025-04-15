package vdi.component.rabbit

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal object RabbitMQConfigDefaults {
  const val Port: UShort = 5672u

  const val ExchangeType = "direct"

  const val ExchangeDurable = true

  const val ExchangeAutoDelete = false

  const val UseTLS = false

  inline val ConnectionTimeout
    get() = 10.seconds

  inline val ExchangeArguments
    get() = emptyMap<String, Any>()

  const val QueueDurable = true

  const val QueueExclusive = false

  const val QueueAutoDelete = false

  inline val QueueArguments
    get() = emptyMap<String, Any>()

  const val RoutingKey = ""

  inline val RoutingArguments
    get() = emptyMap<String, Any>()

  inline val MessagePollingInterval: Duration
    get() = 1.seconds
}