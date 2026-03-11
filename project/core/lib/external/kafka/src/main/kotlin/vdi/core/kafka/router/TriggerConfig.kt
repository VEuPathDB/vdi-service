package vdi.core.kafka.router

data class TriggerConfig(val messageKey: String, val topic: String) {
  override fun toString() = """{ key: $messageKey, topic: $topic }"""
}
