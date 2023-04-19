package vdi.module.handler.imports.triggers

import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig
import vdi.module.handler.imports.triggers.config.loadConfigFromEnvironment

fun ImportTriggerHandler(config: ImportTriggerHandlerConfig = loadConfigFromEnvironment()): ImportTriggerHandler =
  ImportTriggerHandlerImpl(config)