package vdi.lane.delete.hard

fun HardDeleteTriggerHandler(config: HardDeleteTriggerHandlerConfig = HardDeleteTriggerHandlerConfig()): HardDeleteTriggerHandler =
  HardDeleteTriggerHandlerImpl(config)
