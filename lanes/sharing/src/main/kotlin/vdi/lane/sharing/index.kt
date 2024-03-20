package vdi.lane.sharing

fun ShareTriggerHandler(config: ShareTriggerHandlerConfig = ShareTriggerHandlerConfig()): ShareTriggerHandler =
  ShareTriggerHandlerImpl(config)