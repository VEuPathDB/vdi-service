package vdi.lib.rabbit

import vdi.config.raw.rabbit.RabbitQueueConfig

data class QueueConfig(
  val name: String,
  val durable: Boolean,
  val exclusive: Boolean,
  val autoDelete: Boolean,
  val arguments: Map<String, Any>,
) {
  constructor(conf: RabbitQueueConfig): this(
    name = conf.name,
    durable = conf.durable ?: DefaultDurable,
    exclusive = conf.exclusive ?: DefaultExclusive,
    autoDelete = conf.autoDelete ?: DefaultAutoDelete,
    arguments = conf.arguments ?: emptyMap(),
  )

  companion object {
    inline val DefaultDurable get() = true
    inline val DefaultAutoDelete get() = false
    inline val DefaultExclusive get() = false
  }
}
