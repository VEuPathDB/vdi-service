package vdi.core.kafka

@JvmInline
value class MessageKey(val value: String) {
  override fun toString() = value
}

@JvmInline
value class MessageTopic(val value: String) {
  override fun toString() = value
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.toMessageKey() = MessageKey(this)

@Suppress("NOTHING_TO_INLINE")
inline fun String.toMessageTopic() = MessageTopic(this)
