package vdi.lib.config.vdi

import kotlin.time.Duration

data class PrunerConfig(
  val retentionWindow: Duration?,
  val pruneInterval: Duration?,
  val wakeInterval: Duration?,
)
