package vdi.lane.sharing

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun ShareTriggerHandler(config: VDIConfig, abortCB: AbortCB): ShareTriggerHandler =
  ShareTriggerHandler(ShareTriggerHandlerConfig(config), abortCB)

fun ShareTriggerHandler(config: ShareTriggerHandlerConfig, abortCB: AbortCB): ShareTriggerHandler =
  ShareTriggerHandlerImpl(config, abortCB)
