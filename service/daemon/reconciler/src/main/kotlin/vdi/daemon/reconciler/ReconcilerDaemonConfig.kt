package vdi.daemon.reconciler

import org.veupathdb.vdi.lib.common.env.optBool
import org.veupathdb.vdi.lib.common.env.optDuration
import vdi.component.env.EnvKey
import vdi.component.env.Environment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class ReconcilerDaemonConfig(
  val reconcilerEnabled: Boolean,
  val fullRunInterval: Duration,
  val slimRunInterval: Duration,
) {
  constructor() : this(System.getenv())

  constructor(env: Environment) : this(
    reconcilerEnabled = env.optBool(EnvKey.Reconciler.Enabled) ?: DefaultEnabledValue,
    fullRunInterval   = env.optDuration(EnvKey.Reconciler.FullRunInterval) ?: DefaultFullRunInterval,
    slimRunInterval   = env.optDuration(EnvKey.Reconciler.SlimRunInterval) ?: DefaultSlimRunInterval,
  )

  companion object {
    inline val DefaultEnabledValue get() = true
    inline val DefaultFullRunInterval get() = 6.hours
    inline val DefaultSlimRunInterval get() = 5.minutes
  }
}
