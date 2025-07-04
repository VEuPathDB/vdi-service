package vdi.lane.imports

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun ImportLane(config: VDIConfig, abortCB: AbortCB) =
  ImportLane(ImportLaneConfig(config), abortCB)

fun ImportLane(config: ImportLaneConfig, abortCB: AbortCB): ImportLane =
  ImportLaneImpl(config, abortCB)
