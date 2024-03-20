package vdi.lane.install

fun InstallDataTriggerHandler(config: InstallTriggerHandlerConfig = InstallTriggerHandlerConfig()): InstallDataTriggerHandler =
  InstallDataTriggerHandlerImpl(config)