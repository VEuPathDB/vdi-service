package vdi.daemon.reconciler

fun Reconciler(abortCB: (String?) -> Nothing, config: ReconcilerConfig = ReconcilerConfig()): Reconciler =
  ReconcilerImpl(config, abortCB)