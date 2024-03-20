package vdi.module.handler.share.trigger

fun ShareTriggerHandler(config: ShareTriggerHandlerConfig = ShareTriggerHandlerConfig()): ShareTriggerHandler =
  ShareTriggerHandlerImpl(config)