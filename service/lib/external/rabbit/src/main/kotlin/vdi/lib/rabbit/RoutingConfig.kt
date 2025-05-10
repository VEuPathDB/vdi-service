package vdi.lib.rabbit

import vdi.lib.config.vdi.RabbitRoutingConfig

data class RoutingConfig(val key: String, val arguments: Map<String, Any>) {
  constructor(conf: RabbitRoutingConfig?): this(conf?.key ?: DefaultRoutingKey, conf?.arguments ?: emptyMap())
  companion object {
    inline val DefaultRoutingKey get() = ""
  }
}
