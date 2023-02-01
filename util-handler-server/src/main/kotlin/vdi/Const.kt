package vdi

object Const {

  object ExitCode {
    const val ImportScriptSuccess               = 0
    const val ImportScriptValidationFailure     = 1
    const val ImportScriptTransformationFailure = 2
  }

  object ConfigDefault {
    const val ServerPort = "80"
    const val ServerHost = "0.0.0.0"

    const val ImportScriptPath      = "/opt/veupathdb/import"
    const val DataInstallScriptPath = "/opt/veupathdb/install-data"
    const val MetaInstallScriptPath = "/opt/veupathdb/install-meta"
    const val UninstallScriptPath   = "/opt/veupathdb/uninstall"

    const val ImportScriptMaxDuration      = "1h"
    const val DataInstallScriptMaxDuration = "1h"
    const val MetaInstallScriptMaxDuration = "1h"
    const val UninstallScriptMaxDuration   = "1h"
  }

  object EnvKey {
    const val ImportScriptPath = "IMPORT_SCRIPT_PATH"
    const val ImportScriptMaxDuration = "IMPORT_SCRIPT_MAX_DURATION"

    const val DataInstallScriptPath = "INSTALL_DATA_SCRIPT_PATH"
    const val DataInstallScriptMaxDuration = "INSTALL_DATA_SCRIPT_MAX_DURATION"

    const val MetaInstallScriptPath = "INSTALL_META_SCRIPT_PATH"
    const val MetaInstallScriptMaxDuration = "INSTALL_META_SCRIPT_MAX_DURATION"

    const val UninstallScriptPath = "UNINSTALL_SCRIPT_PATH"
    const val UninstallScriptMaxDuration = "UNINSTALL_SCRIPT_MAX_DURATION"

    const val ServerPort = "SERVER_PORT"
    const val ServerHost = "SERVER_HOST"
  }
}