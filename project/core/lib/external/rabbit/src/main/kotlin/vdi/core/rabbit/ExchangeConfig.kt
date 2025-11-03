package vdi.core.rabbit

import vdi.core.config.rabbit.RabbitExchangeConfig

data class ExchangeConfig(
  val name: String,
  val type: String,
  val durable: Boolean,
  val autoDelete: Boolean,
  val arguments: Map<String, Any>,
) {
  constructor(conf: RabbitExchangeConfig): this(
    name = conf.name,
    type = conf.type ?: DefaultType,
    durable = conf.durable ?: DefaultDurable,
    autoDelete = conf.autoDelete ?: DefaultAutoDelete,
    arguments = conf.arguments ?: emptyMap(),
  )

  companion object {
    inline val DefaultType get() = "direct"
    inline val DefaultDurable get() = true
    inline val DefaultAutoDelete get() = false
  }
}
