package vdi.lib.config.vdi.daemons

data class DaemonConfig(
  val eventRouter: EventRouterConfig?,
  val pruner: PrunerConfig?,
  val reconciler: ReconcilerConfig?,
)
