package vdi.lane.delete.soft

fun SoftDeleteTriggerHandler(config: SoftDeleteTriggerHandlerConfig = SoftDeleteTriggerHandlerConfig()): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandlerImpl(config)