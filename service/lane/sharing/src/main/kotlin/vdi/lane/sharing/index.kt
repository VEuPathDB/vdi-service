package vdi.lane.sharing

fun ShareTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: ShareTriggerHandlerConfig = ShareTriggerHandlerConfig()
): ShareTriggerHandler =
  ShareTriggerHandlerImpl(config, abortCB)