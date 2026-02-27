package vdi.core.config.vdi.daemons

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

data class ReconcilerConfig(
  val enabled: Boolean = true,
  val fullRunInterval: Duration = 6.hours,
  val slimRunInterval: Duration = 5.minutes,
  val performDeletes: Boolean = true,
) {
  val javaFullRunInterval get() = fullRunInterval.toJavaDuration()
  val javaSlimRunInterval get() = slimRunInterval.toJavaDuration()
}
