package vdi.lane.delete.soft

fun SoftDeleteTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: SoftDeleteTriggerHandlerConfig = SoftDeleteTriggerHandlerConfig()
): SoftDeleteTriggerHandler =
  SoftDeleteTriggerHandlerImpl(config, abortCB)