package vdi.lane.hard_delete

import vdi.lib.config.vdi.VDIConfig
import vdi.lib.modules.AbortCB

fun HardDeleteTriggerHandler(
  config: VDIConfig,
  abortCB: AbortCB,
): HardDeleteTriggerHandler =
  HardDeleteTriggerHandler(HardDeleteTriggerHandlerConfig(config), abortCB)

fun HardDeleteTriggerHandler(
  config: HardDeleteTriggerHandlerConfig,
  abortCB: AbortCB,
): HardDeleteTriggerHandler =
  HardDeleteTriggerHandlerImpl(config, abortCB)
