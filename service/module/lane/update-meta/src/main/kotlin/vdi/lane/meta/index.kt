package vdi.lane.meta

fun UpdateMetaTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: UpdateMetaTriggerHandlerConfig = UpdateMetaTriggerHandlerConfig()
): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(config, abortCB)