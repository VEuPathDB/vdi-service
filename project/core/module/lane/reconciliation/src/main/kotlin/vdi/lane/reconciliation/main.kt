package vdi.lane.reconciliation

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun ReconciliationLane(config: VDIConfig, abortCB: AbortCB): ReconciliationLane =
  ReconciliationLaneImpl(ReconciliationLaneConfig(config), abortCB)
