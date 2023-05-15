package vdi.module.handler.delete.soft

import vdi.module.handler.delete.soft.config.SoftDeleteTriggerHandlerConfig
import vdi.module.handler.delete.soft.config.loadConfigFromEnvironment

fun SoftDeleteTriggerHandler(config: SoftDeleteTriggerHandlerConfig = loadConfigFromEnvironment()): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandlerImpl(config)