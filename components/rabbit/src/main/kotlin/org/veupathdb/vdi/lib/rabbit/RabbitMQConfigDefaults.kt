package org.veupathdb.vdi.lib.rabbit

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal object RabbitMQConfigDefaults {
  const val Port: UShort = 5672u

  const val ExchangeType = "direct"

  const val ExchangeDurable = true

  const val ExchangeAutoDelete = false

  const val UseTLS = false

  inline val ExchangeArguments: Map<String, Any>
    get() = emptyMap()

  const val QueueDurable = true

  const val QueueExclusive = false

  const val QueueAutoDelete = false

  inline val QueueArguments: Map<String, Any>
    get() = emptyMap()

  const val RoutingKey = ""

  inline val RoutingArguments: Map<String, Any>
    get() = emptyMap()

  inline val MessagePollingInterval: Duration
    get() = 1.seconds
}