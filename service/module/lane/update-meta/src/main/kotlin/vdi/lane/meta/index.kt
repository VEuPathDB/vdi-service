package vdi.lane.meta

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun UpdateMetaTriggerHandler(config: VDIConfig, abortCB: AbortCB): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(UpdateMetaTriggerHandlerConfig(config), abortCB)

fun UpdateMetaTriggerHandler(config: UpdateMetaTriggerHandlerConfig, abortCB: AbortCB): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(config, abortCB)
