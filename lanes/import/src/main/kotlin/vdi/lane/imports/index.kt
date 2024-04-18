package vdi.lane.imports

import vdi.lane.imports.config.ImportTriggerHandlerConfig

fun ImportTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: ImportTriggerHandlerConfig = ImportTriggerHandlerConfig()
): ImportTriggerHandler =
  ImportTriggerHandlerImpl(config, abortCB)
