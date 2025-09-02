package vdi.lane.sharing

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun ShareLane(config: VDIConfig, abortCB: AbortCB): ShareLane =
  ShareLaneImpl(ShareLaneConfig(config), abortCB)
