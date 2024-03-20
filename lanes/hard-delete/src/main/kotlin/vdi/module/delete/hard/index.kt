package vdi.module.delete.hard

fun HardDeleteTriggerHandler(config: HardDeleteTriggerHandlerConfig = HardDeleteTriggerHandlerConfig()): HardDeleteTriggerHandler =
  HardDeleteTriggerHandlerImpl(config)
