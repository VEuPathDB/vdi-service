package vdi.module.handler.install.data

fun InstallDataTriggerHandler(config: InstallTriggerHandlerConfig = InstallTriggerHandlerConfig()): InstallDataTriggerHandler =
  InstallDataTriggerHandlerImpl(config)