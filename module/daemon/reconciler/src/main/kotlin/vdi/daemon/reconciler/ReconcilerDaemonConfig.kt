package vdi.daemon.reconciler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import vdi.lib.config.vdi.daemons.ReconcilerConfig

data class ReconcilerDaemonConfig(
  val reconcilerEnabled: Boolean,
  val fullRunInterval: Duration,
  val slimRunInterval: Duration,
) {
  constructor(config: ReconcilerConfig?): this(
    reconcilerEnabled = config?.enabled ?: DefaultEnabledValue,
    fullRunInterval   = config?.fullRunInterval ?: DefaultFullRunInterval,
    slimRunInterval   = config?.slimRunInterval ?: DefaultSlimRunInterval,
  )

  companion object {
    inline val DefaultEnabledValue get() = true
    inline val DefaultFullRunInterval get() = 6.hours
    inline val DefaultSlimRunInterval get() = 5.minutes
  }
}
