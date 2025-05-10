package vdi.lane.delete.hard

fun HardDeleteTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: HardDeleteTriggerHandlerConfig = HardDeleteTriggerHandlerConfig()
): HardDeleteTriggerHandler =
  HardDeleteTriggerHandlerImpl(config, abortCB)
