package vdi.lane.install

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB


fun InstallDataLane(config: VDIConfig, abortCB: AbortCB) =
  InstallDataLane(InstallDataLaneConfig(config), abortCB)

fun InstallDataLane(config: InstallDataLaneConfig, abortCB: AbortCB): InstallDataLane =
  InstallDataLaneImpl(config, abortCB)
