package vdi.daemon.reinstaller

import org.veupathdb.vdi.lib.common.env.optDuration
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class DatasetReinstallerConfig(
  val runInterval:  Duration,
  val wakeInterval: Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    runInterval  = env.optDuration(EnvKey.DatasetReinstaller.RunInterval)
      ?: Defaults.RunInterval,
    wakeInterval = env.optDuration(EnvKey.DatasetReinstaller.WakeInterval)
      ?: Defaults.WakeInterval,
  )

  object Defaults {
    inline val RunInterval
      get() = 6.hours

    inline val WakeInterval
      get() = 2.seconds
  }
}
