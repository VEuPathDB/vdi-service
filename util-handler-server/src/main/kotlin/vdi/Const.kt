package vdi

object Const {

  object ExitCode {
    const val IMPORT_SCRIPT_SUCCESS_CODE = 0
    const val IMPORT_SCRIPT_FAIL_VALIDATION_CODE = 1
    const val IMPORT_SCRIPT_FAIL_TRANSFORMATION_CODE = 2
    const val IMPORT_SCRIPT_FAIL_UNEXPECTED_CODE = 3
  }

  object ConfigDefault {
    const val SERVER_PORT = "80"
    const val SERVER_HOST = "0.0.0.0"

    const val IMPORT_SCRIPT_PATH = "/opt/veupathdb/import"
    const val DATA_INSTALL_SCRIPT_PATH = "/opt/veupathdb/install-data"
    const val META_INSTALL_SCRIPT_PATH = "/opt/veupathdb/install-meta"
    const val UNINSTALL_SCRIPT_PATH = "/opt/veupathdb/uninstall"

    const val IMPORT_SCRIPT_MAX_DURATION = "1h"
    const val INSTALL_DATA_SCRIPT_MAX_DURATION = "1h"
    const val INSTALL_META_SCRIPT_MAX_DURATION = "1h"
    const val UNINSTALL_SCRIPT_MAX_DURATION = "1h"
  }

  object EnvKey {
    const val IMPORT_SCRIPT_PATH = "IMPORT_SCRIPT_PATH"
    const val IMPORT_SCRIPT_MAX_DURATION = "IMPORT_SCRIPT_MAX_DURATION"

    const val INSTALL_DATA_SCRIPT_PATH = "INSTALL_DATA_SCRIPT_PATH"
    const val INSTALL_DATA_SCRIPT_MAX_DURATION = "INSTALL_DATA_SCRIPT_MAX_DURATION"

    const val INSTALL_META_SCRIPT_PATH = "INSTALL_META_SCRIPT_PATH"
    const val INSTALL_META_SCRIPT_MAX_DURATION = "INSTALL_META_SCRIPT_MAX_DURATION"

    const val UNINSTALL_SCRIPT_PATH = "UNINSTALL_SCRIPT_PATH"
    const val UNINSTALL_SCRIPT_MAX_DURATION = "UNINSTALL_SCRIPT_MAX_DURATION"

    const val SERVER_PORT = "SERVER_PORT"
    const val SERVER_HOST = "SERVER_HOST"
  }
}