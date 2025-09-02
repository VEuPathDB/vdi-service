package vdi.lane.install

import vdi.core.config.vdi.VDIConfig
import vdi.core.modules.AbortCB


fun InstallDataLane(config: VDIConfig, abortCB: AbortCB): InstallDataLane =
  InstallDataLaneImpl(InstallDataLaneConfig(config), abortCB)
