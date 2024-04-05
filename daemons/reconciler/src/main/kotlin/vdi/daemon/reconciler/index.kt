package vdi.daemon.reconciler

fun Reconciler(config: ReconcilerConfig = ReconcilerConfig()): Reconciler = ReconcilerImpl(config)