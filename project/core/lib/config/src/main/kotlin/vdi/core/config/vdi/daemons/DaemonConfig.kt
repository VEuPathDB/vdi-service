package vdi.core.config.vdi.daemons

data class DaemonConfig(
  val eventRouter: EventRouterConfig = EventRouterConfig(),
  val pruner: PrunerConfig = PrunerConfig(),
  val reconciler: ReconcilerConfig = ReconcilerConfig(),
)
