package vdi.lane.reconciliation

fun ReconciliationEventHandler(
  abortCB: (String?) -> Nothing,
  config: ReconciliationEventHandlerConfig = ReconciliationEventHandlerConfig()
): ReconciliationEventHandler =
  ReconciliationEventHandlerImpl(config, abortCB)