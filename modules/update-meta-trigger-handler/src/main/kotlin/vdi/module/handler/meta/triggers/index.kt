package vdi.module.handler.meta.triggers

import vdi.module.handler.meta.triggers.config.UpdateMetaTriggerHandlerConfig
import vdi.module.handler.meta.triggers.config.loadConfigFromEnvironment

fun UpdateMetaTriggerHandler(config: UpdateMetaTriggerHandlerConfig = loadConfigFromEnvironment()): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(config)