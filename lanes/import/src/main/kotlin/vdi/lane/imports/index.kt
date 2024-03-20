package vdi.lane.imports

import vdi.lane.imports.config.ImportTriggerHandlerConfig

fun ImportTriggerHandler(config: ImportTriggerHandlerConfig = ImportTriggerHandlerConfig()): ImportTriggerHandler =
  ImportTriggerHandlerImpl(config)
