package vdi.config.raw.vdi.daemons

data class DaemonConfig(
  val eventRouter: EventRouterConfig?,
  val pruner: PrunerConfig?,
  val reconciler: ReconcilerConfig?,
)
