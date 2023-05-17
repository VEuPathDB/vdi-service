package org.veupathdb.vdi.lib.kafka

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object KafkaConsumerConfigDefaults {
  inline val FetchMinBytes
    get() = 1

  inline val HeartbeatInterval
    get() = 3.seconds

  inline val SessionTimeout
    get() = 45.seconds

  inline val AutoOffsetReset
    get() = KafkaOffsetType.EARLIEST

  inline val ConnectionsMaxIdle
    get() = 9.minutes

  inline val DefaultAPITimeout
    get() = 1.minutes

  inline val EnableAutoCommit
    get() = true

  inline val FetchMaxBytes
    get() = 52428800

  inline val GroupInstanceID
    get() = null as String?

  inline val MaxPollInterval
    get() = 5.minutes

  inline val MaxPollRecords
    get() = 500

  inline val ReceiveBufferSizeBytes
    get() = 65536

  inline val RequestTimeout
    get() = 30.seconds

  inline val SendBufferSizeBytes
    get() = 131072

  inline val AutoCommitInterval
    get() = 5.seconds

  inline val ReconnectBackoffMaxTime
    get() = 1.seconds

  inline val ReconnectBackoffTime
    get() = 50.milliseconds

  inline val RetryBackoffTime
    get() = 100.milliseconds

  inline val PollDuration
    get() = 2.seconds
}