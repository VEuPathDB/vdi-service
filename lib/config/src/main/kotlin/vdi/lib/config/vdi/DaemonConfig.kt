package vdi.lib.config.vdi

data class DaemonConfig(
  val eventRouter: EventRouterConfig?,
  val pruner: PrunerConfig?,
  val reconciler: ReconcilerConfig?,
)
