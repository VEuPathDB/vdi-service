package vdi.daemon.reinstaller

fun DatasetReinstaller(config: DatasetReinstallerConfig = DatasetReinstallerConfig()): DatasetReinstaller =
  DatasetReinstallerImpl(config)