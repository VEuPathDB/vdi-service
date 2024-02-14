package vdi.module.handler.meta.triggers

fun UpdateMetaTriggerHandler(config: UpdateMetaTriggerHandlerConfig = UpdateMetaTriggerHandlerConfig()): UpdateMetaTriggerHandler =
  UpdateMetaTriggerHandlerImpl(config)