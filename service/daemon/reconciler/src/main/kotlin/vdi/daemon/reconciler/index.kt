package vdi.daemon.reconciler

import vdi.lib.modules.AbortCB

fun Reconciler(config: ReconcilerDaemonConfig = ReconcilerDaemonConfig(), abortCB: AbortCB): Reconciler =
  ReconcilerImpl(config, abortCB)
