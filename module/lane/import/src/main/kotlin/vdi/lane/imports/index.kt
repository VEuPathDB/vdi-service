package vdi.lane.imports

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun ImportLane(config: VDIConfig, abortCB: AbortCB) =
  ImportLane(ImportLaneConfig(config), abortCB)

fun ImportLane(config: ImportLaneConfig, abortCB: AbortCB): ImportLane =
  ImportLaneImpl(config, abortCB)
