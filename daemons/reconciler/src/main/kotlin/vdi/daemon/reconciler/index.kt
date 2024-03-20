package vdi.daemon.reconciler

import vdi.daemon.reconciler.config.ReconcilerConfig
import vdi.daemon.reconciler.config.loadConfigFromEnvironment

fun Reconciler(config: ReconcilerConfig = loadConfigFromEnvironment()): Reconciler = ReconcilerImpl(config)