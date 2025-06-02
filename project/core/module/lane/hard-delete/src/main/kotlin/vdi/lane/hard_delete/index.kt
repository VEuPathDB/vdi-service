package vdi.lane.hard_delete

import vdi.config.raw.vdi.VDIConfig
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
