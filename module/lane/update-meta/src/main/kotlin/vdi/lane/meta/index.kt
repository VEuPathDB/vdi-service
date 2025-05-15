package vdi.lane.meta

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun UpdateMetaLane(config: VDIConfig, abortCB: AbortCB): UpdateMetaLane =
  UpdateMetaLaneImpl(UpdateMetaLaneConfig(config), abortCB)

fun UpdateMetaLane(config: UpdateMetaLaneConfig, abortCB: AbortCB): UpdateMetaLane =
  UpdateMetaLaneImpl(config, abortCB)
