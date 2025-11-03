package vdi.core.config.vdi.daemons

import kotlin.time.Duration

data class ReconcilerConfig(
  val enabled: Boolean?,
  val fullRunInterval: Duration?,
  val slimRunInterval: Duration?,
  val performDeletes: Boolean?,
)
