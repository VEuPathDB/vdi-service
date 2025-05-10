package vdi.lib.config.vdi.daemons

import kotlin.time.Duration

data class PrunerConfig(
  val retentionWindow: Duration?,
  val pruneInterval: Duration?,
  val wakeInterval: Duration?,
)
