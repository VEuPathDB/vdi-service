package vdi.lane.soft_delete

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun SoftDeleteLane(config: VDIConfig, abortCB: AbortCB): SoftDeleteLane =
  SoftDeleteLane(SoftDeleteLaneConfig(config), abortCB)

fun SoftDeleteLane(config: SoftDeleteLaneConfig, abortCB: AbortCB): SoftDeleteLane =
  SoftDeleteLaneImpl(config, abortCB)
