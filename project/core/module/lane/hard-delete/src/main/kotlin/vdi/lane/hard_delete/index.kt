package vdi.lane.hard_delete

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

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
