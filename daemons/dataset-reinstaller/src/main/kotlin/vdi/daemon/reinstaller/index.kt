package vdi.daemon.reinstaller

fun DatasetReinstaller(
  abortCB: (String?) -> Nothing,
  config: DatasetReinstallerConfig = DatasetReinstallerConfig(),
): DatasetReinstaller =
  DatasetReinstallerImpl(config, abortCB)