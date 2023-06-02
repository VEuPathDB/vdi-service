package vdi.module.reinstaller

fun DatasetReinstaller(config: DatasetReinstallerConfig = DatasetReinstallerConfig()): DatasetReinstaller =
  DatasetReinstallerImpl(config)