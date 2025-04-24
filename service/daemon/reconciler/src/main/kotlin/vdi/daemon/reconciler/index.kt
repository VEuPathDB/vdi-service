package vdi.daemon.reconciler

import vdi.lib.modules.AbortCB

fun Reconciler(abortCB: AbortCB, config: ReconcilerDaemonConfig = ReconcilerDaemonConfig()): Reconciler =
  ReconcilerImpl(config, abortCB)
