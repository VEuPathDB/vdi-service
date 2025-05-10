package vdi.lib.kafka.router

import vdi.lib.kafka.MessageKey
import vdi.lib.kafka.MessageTopic

data class TriggerConfig(val messageKey: MessageKey, val topic: MessageTopic)
