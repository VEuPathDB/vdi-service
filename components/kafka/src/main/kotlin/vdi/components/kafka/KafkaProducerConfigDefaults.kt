package vdi.components.kafka

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object KafkaProducerConfigDefaults {
  inline val BUFFER_MEMORY_BYTES
    get() = 33554432L

  inline val COMPRESSION_TYPE
    get() = KafkaCompressionType.NONE

  inline val SEND_RETRIES
    get() = 2147483647

  inline val BATCH_SIZE
    get() = 16384

  inline val CONNECTIONS_MAX_IDLE
    get() = 9.minutes

  inline val DELIVERY_TIMEOUT
    get() = 2.minutes

  inline val LINGER_TIME
    get() = 0.milliseconds

  inline val MAX_BLOCKING_TIMEOUT
    get() = 1.minutes

  inline val MAX_REQUEST_SIZE_BYTES
    get() = 1048576

  inline val RECEIVE_BUFFER_SIZE_BYTES
    get() = 32768

  inline val REQUEST_TIMEOUT
    get() = 30.seconds

  inline val SEND_BUFFER_SIZE_BYTES
    get() = 131072

  inline val RECONNECT_BACKOFF_MAX_TIME
    get() = 1.seconds

  inline val RECONNECT_BACKOFF_TIME
    get() = 50.milliseconds

  inline val RETRY_BACKOFF_TIME
    get() = 100.milliseconds
}