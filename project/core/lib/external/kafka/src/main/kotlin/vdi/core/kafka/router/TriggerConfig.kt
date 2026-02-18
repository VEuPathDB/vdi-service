package vdi.core.kafka.router

import vdi.core.kafka.MessageKey
import vdi.core.kafka.MessageTopic

data class TriggerConfig(val messageKey: MessageKey, val topic: MessageTopic) {
  override fun toString() = """{ key: $messageKey, topic: $topic }"""
}
