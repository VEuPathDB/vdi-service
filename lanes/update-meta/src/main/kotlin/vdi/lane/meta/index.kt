package vdi.lane.meta

fun UpdateMetaTriggerHandler(config: UpdateMetaTriggerHandlerConfig = UpdateMetaTriggerHandlerConfig()): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(config)