package vdi.lane.reconciliation

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun ReconciliationEventHandler(config: VDIConfig, abortCB: AbortCB) =
  ReconciliationEventHandler(ReconciliationEventHandlerConfig(config), abortCB)

fun ReconciliationEventHandler(config: ReconciliationEventHandlerConfig, abortCB: AbortCB): ReconciliationEventHandler =
  ReconciliationEventHandlerImpl(config, abortCB)
