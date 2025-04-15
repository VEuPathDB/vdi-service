package vdi.lib.reconciler

internal interface SyncReason {
  class OutOfSync(val meta: Boolean, val shares: Boolean, val install: Boolean, val target: ReconcilerTarget) :
  SyncReason {
    override fun toString() =
      "out of sync:" +
      (if (meta) " meta" else "") +
      (if (shares) " shares" else "") +
      (if (install) " install" else "") +
      " in target " + target.name
  }

  class MissingInTarget(val target: ReconcilerTarget) : SyncReason {
    override fun toString() = "missing from install target ${target.name}"
  }

  class NeedsUninstall(val target: ReconcilerTarget) : SyncReason {
    override fun toString() = "needs uninstallation from target ${target.name}"
  }
}
