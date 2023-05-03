package vdi.module.handler.share.trigger

import vdi.module.handler.share.trigger.config.ShareTriggerHandlerConfig
import vdi.module.handler.share.trigger.config.loadConfigFromEnvironment

fun ShareTriggerHandler(config: ShareTriggerHandlerConfig = loadConfigFromEnvironment()): ShareTriggerHandler =
  ShareTriggerHandlerImpl(config)