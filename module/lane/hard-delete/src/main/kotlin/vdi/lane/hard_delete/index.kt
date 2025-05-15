package vdi.lane.hard_delete

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun HardDeleteLane(
  config: VDIConfig,
  abortCB: AbortCB,
): HardDeleteLane =
  HardDeleteLane(HardDeleteLaneConfig(config), abortCB)

fun HardDeleteLane(
  config: HardDeleteLaneConfig,
  abortCB: AbortCB,
): HardDeleteLane =
  HardDeleteLaneImpl(config, abortCB)
