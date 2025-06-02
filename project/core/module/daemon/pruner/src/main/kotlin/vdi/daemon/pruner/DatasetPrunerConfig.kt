package vdi.daemon.pruner

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import vdi.config.raw.vdi.daemons.PrunerConfig

data class DatasetPrunerConfig(
  val pruningInterval: Duration,
  val wakeupInterval: Duration,
) {
  constructor(conf: PrunerConfig?) : this(
    pruningInterval = conf?.pruneInterval ?: Defaults.PruningInterval,
    wakeupInterval  = conf?.wakeInterval ?: Defaults.WakeupInterval
  )

  object Defaults {
    inline val PruningInterval
      get() = 6.hours

    inline val WakeupInterval
      get() = 2.seconds
  }
}
