package vdi.core.config.vdi

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class SharedPoolConfig(
  val poolSize: UByte = 20u,
  val idleTimeout: Duration = 10.minutes,
)
