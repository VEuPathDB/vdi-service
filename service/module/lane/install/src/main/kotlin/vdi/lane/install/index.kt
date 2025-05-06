package vdi.lane.install

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB


fun InstallDataTriggerHandler(config: VDIConfig, abortCB: AbortCB) =
  InstallDataTriggerHandler(InstallTriggerHandlerConfig(config), abortCB)

fun InstallDataTriggerHandler(config: InstallTriggerHandlerConfig, abortCB: AbortCB): InstallDataTriggerHandler =
  InstallDataTriggerHandlerImpl(config, abortCB)
