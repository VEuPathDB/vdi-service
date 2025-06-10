package vdi.lane.soft_delete

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun SoftDeleteLane(config: VDIConfig, abortCB: AbortCB): SoftDeleteLane =
  SoftDeleteLane(SoftDeleteLaneConfig(config), abortCB)

fun SoftDeleteLane(config: SoftDeleteLaneConfig, abortCB: AbortCB): SoftDeleteLane =
  SoftDeleteLaneImpl(config, abortCB)
