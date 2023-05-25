package vdi.module.handler.delete.soft

fun SoftDeleteTriggerHandler(config: SoftDeleteTriggerHandlerConfig = SoftDeleteTriggerHandlerConfig()): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandlerImpl(config)