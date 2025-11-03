package vdi.daemon.reconciler

import vdi.core.config.vdi.daemons.ReconcilerConfig
import vdi.core.modules.AbortCB

fun Reconciler(config: ReconcilerConfig?, abortCB: AbortCB): Reconciler =
  ReconcilerImpl(ReconcilerDaemonConfig(config), abortCB)

fun Reconciler(config: ReconcilerDaemonConfig, abortCB: AbortCB): Reconciler =
  ReconcilerImpl(config, abortCB)
