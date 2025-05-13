package vdi.lane.sharing

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun ShareLane(config: VDIConfig, abortCB: AbortCB): ShareLane =
  ShareLane(ShareLaneConfig(config), abortCB)

fun ShareLane(config: ShareLaneConfig, abortCB: AbortCB): ShareLane =
  ShareLaneImpl(config, abortCB)
