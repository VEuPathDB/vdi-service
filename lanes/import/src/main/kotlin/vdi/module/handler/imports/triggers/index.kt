package vdi.module.handler.imports.triggers

import vdi.module.handler.imports.triggers.config.ImportTriggerHandlerConfig

fun ImportTriggerHandler(config: ImportTriggerHandlerConfig = ImportTriggerHandlerConfig()): ImportTriggerHandler =
  ImportTriggerHandlerImpl(config)
