package vdi.daemon.reconciler

import vdi.component.modules.AbortCB

fun Reconciler(config: ReconcilerDaemonConfig = ReconcilerDaemonConfig(), abortCB: AbortCB): Reconciler =
  ReconcilerImpl(config, abortCB)