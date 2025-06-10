package vdi.lane.sharing

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun ShareLane(config: VDIConfig, abortCB: AbortCB): ShareLane =
  ShareLane(ShareLaneConfig(config), abortCB)

fun ShareLane(config: ShareLaneConfig, abortCB: AbortCB): ShareLane =
  ShareLaneImpl(config, abortCB)
