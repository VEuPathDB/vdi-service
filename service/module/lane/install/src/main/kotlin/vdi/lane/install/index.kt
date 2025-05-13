package vdi.lane.install

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB


fun InstallDataLane(config: VDIConfig, abortCB: AbortCB) =
  InstallDataLane(InstallDataLaneConfig(config), abortCB)

fun InstallDataLane(config: InstallDataLaneConfig, abortCB: AbortCB): InstallDataLane =
  InstallDataLaneImpl(config, abortCB)
