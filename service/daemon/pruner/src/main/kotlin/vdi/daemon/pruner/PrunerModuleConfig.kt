package vdi.daemon.pruner

import org.veupathdb.vdi.lib.common.env.optDuration
import vdi.lib.env.EnvKey
import vdi.lib.env.Environment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class PrunerModuleConfig(
  val pruningInterval: Duration,
  val wakeupInterval: Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    pruningInterval = env.optDuration(EnvKey.DeletedDatasetPruner.PruningInterval)
      ?: Defaults.PruningInterval,
    wakeupInterval = env.optDuration(EnvKey.DeletedDatasetPruner.PruningWakeupInterval)
      ?: Defaults.WakeupInterval
  )

  object Defaults {
    inline val PruningInterval
      get() = 6.hours

    inline val WakeupInterval
      get() = 2.seconds
  }
}
