package vdi.lane.imports

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun ImportLane(config: VDIConfig, abortCB: AbortCB): ImportLane =
  ImportLaneImpl(ImportLaneConfig(config), abortCB)
