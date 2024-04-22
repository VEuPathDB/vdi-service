package vdi.component.pruner

import kotlin.time.Duration.Companion.days

object PrunerConfigDefaults {
  inline val DeletionThreshold
    get() = 5.days
}