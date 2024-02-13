package vdi.module.pruner

import org.veupathdb.vdi.lib.common.env.EnvKey
import org.veupathdb.vdi.lib.common.env.Environment
import org.veupathdb.vdi.lib.common.env.optDuration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class PrunerModuleConfig(
  val pruningInterval: Duration,
  val wakeupInterval: Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    pruningInterval = env.optDuration(EnvKey.Pruner.PruningInterval)
      ?: Defaults.PruningInterval,
    wakeupInterval = env.optDuration(EnvKey.Pruner.PruningWakeupInterval)
      ?: Defaults.WakeupInterval
  )

  object Defaults {
    inline val PruningInterval
      get() = 6.hours

    inline val WakeupInterval
      get() = 5.seconds
  }
}