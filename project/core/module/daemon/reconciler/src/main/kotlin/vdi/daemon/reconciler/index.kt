package vdi.daemon.reconciler

import vdi.config.raw.vdi.daemons.ReconcilerConfig
import vdi.lib.modules.AbortCB

fun Reconciler(config: ReconcilerConfig?, abortCB: AbortCB): Reconciler =
  ReconcilerImpl(ReconcilerDaemonConfig(config), abortCB)

fun Reconciler(config: ReconcilerDaemonConfig, abortCB: AbortCB): Reconciler =
  ReconcilerImpl(config, abortCB)
