package vdi.lane.install

fun InstallDataTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: InstallTriggerHandlerConfig = InstallTriggerHandlerConfig()
): InstallDataTriggerHandler =
  InstallDataTriggerHandlerImpl(config, abortCB)