package vdi.core.rabbit

import vdi.core.config.rabbit.RabbitRoutingConfig

data class RoutingConfig(val key: String, val arguments: Map<String, Any>) {
  constructor(conf: RabbitRoutingConfig?): this(conf?.key ?: DefaultRoutingKey, conf?.arguments ?: emptyMap())
  companion object {
    inline val DefaultRoutingKey get() = ""
  }
}
