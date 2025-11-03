package vdi.core.config.vdi.daemons

data class DaemonConfig(
  val eventRouter: EventRouterConfig?,
  val pruner: PrunerConfig?,
  val reconciler: ReconcilerConfig?,
)
