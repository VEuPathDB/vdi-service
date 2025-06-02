package vdi.lane.meta

import vdi.config.raw.vdi.VDIConfig
import vdi.core.modules.AbortCB

fun UpdateMetaLane(config: VDIConfig, abortCB: AbortCB): UpdateMetaLane =
  UpdateMetaLaneImpl(UpdateMetaLaneConfig(config), abortCB)

fun UpdateMetaLane(config: UpdateMetaLaneConfig, abortCB: AbortCB): UpdateMetaLane =
  UpdateMetaLaneImpl(config, abortCB)
