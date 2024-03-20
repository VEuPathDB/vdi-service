package vdi.lane.reconciliation

fun ReconciliationEventHandler(config: ReconciliationEventHandlerConfig = ReconciliationEventHandlerConfig()): ReconciliationEventHandler =
  ReconciliationEventHandlerImpl(config)