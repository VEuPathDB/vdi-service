package vdi.core.config.vdi.daemons

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

data class PrunerConfig(
  val retentionWindow: Duration = 30.days,
  val pruneInterval: Duration = 6.hours,
  val wakeInterval: Duration = 2.seconds,
)
