package vdi.lib.kafka

@JvmInline
value class MessageKey(val value: String)

@JvmInline
value class MessageTopic(val value: String)

@Suppress("NOTHING_TO_INLINE")
inline fun String.toMessageKey() = MessageKey(this)

@Suppress("NOTHING_TO_INLINE")
inline fun String.toMessageTopic() = MessageTopic(this)
