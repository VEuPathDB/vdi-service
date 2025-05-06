package vdi.lane.delete.soft

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun SoftDeleteTriggerHandler(config: VDIConfig, abortCB: AbortCB): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandler(SoftDeleteTriggerHandlerConfig(config), abortCB)

fun SoftDeleteTriggerHandler(config: SoftDeleteTriggerHandlerConfig, abortCB: AbortCB): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandlerImpl(config, abortCB)
