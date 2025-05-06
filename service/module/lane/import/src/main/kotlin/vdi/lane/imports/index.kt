package vdi.lane.imports

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun ImportTriggerHandler(config: VDIConfig, abortCB: AbortCB) =
  ImportTriggerHandler(ImportTriggerHandlerConfig(config), abortCB)

fun ImportTriggerHandler(config: ImportTriggerHandlerConfig, abortCB: AbortCB): ImportTriggerHandler =
  ImportTriggerHandlerImpl(config, abortCB)
