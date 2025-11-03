package vdi.core.config.rabbit

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import kotlin.time.Duration

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "global")
data class RabbitConfig(
  val pollInterval: Duration?,
  val connection: RabbitConnectionConfig,
  val exchange: RabbitExchangeConfig,
  val queue: RabbitQueueConfig,
  val routing: RabbitRoutingConfig?,
)
