package vdi.config.raw.vdi.daemons

import kotlin.time.Duration

data class PrunerConfig(
  val retentionWindow: Duration?,
  val pruneInterval: Duration?,
  val wakeInterval: Duration?,
)
