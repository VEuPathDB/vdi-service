package vdi.lane.prune.revision

fun RevisionPruningTriggerHandler(
  abortCB: (String?) -> Nothing,
  config: RevisionPruningTriggerHandlerConfig = RevisionPruningTriggerHandlerConfig()
): RevisionPruningTriggerHandler =
  RevisionPruningTriggerHandlerImpl(config, abortCB)
