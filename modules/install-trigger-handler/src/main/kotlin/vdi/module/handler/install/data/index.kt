package vdi.module.handler.install.data

import vdi.module.handler.install.data.config.InstallTriggerHandlerConfig

fun InstallDataTriggerHandler(config: InstallTriggerHandlerConfig = InstallTriggerHandlerConfig()): InstallDataTriggerHandler =
  InstallDataTriggerHandlerImpl(config)