package vdi.conf

import kotlin.time.Duration

private const val DEFAULT_IMPORT_SCRIPT_PATH       = "/opt/veupathdb/import"
private const val DEFAULT_DATA_INSTALL_SCRIPT_PATH = "/opt/veupathdb/install-data"
private const val DEFAULT_META_INSTALL_SCRIPT_PATH = "/opt/veupathdb/install-meta"
private const val DEFAULT_UNINSTALL_SCRIPT_PATH    = "/opt/veupathdb/uninstall"

private const val DEFAULT_IMPORT_SCRIPT_MAX_DURATION = "1h"

/**
 * Service Specific Configuration Options
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
data class ServiceConfiguration(
  val importScriptPath: String,
  val importScriptMaxSeconds: Long,

  val installDataScriptPath: String,
  val installMetaScriptPath: String,
  val uninstallScriptPath:   String,
) {
  constructor(environment: Map<String, String> = System.getenv()) : this(
    importScriptPath = environment["IMPORT_SCRIPT_PATH"] ?: DEFAULT_IMPORT_SCRIPT_PATH,

    importScriptMaxSeconds = Duration.parse(
      environment["IMPORT_SCRIPT_MAX_DURATION"] ?: DEFAULT_IMPORT_SCRIPT_MAX_DURATION
    ).inWholeSeconds,

    installDataScriptPath = environment["INSTALL_DATA_SCRIPT_PATH"] ?: DEFAULT_DATA_INSTALL_SCRIPT_PATH,

    installMetaScriptPath = environment["INSTALL_META_SCRIPT_PATH"] ?: DEFAULT_META_INSTALL_SCRIPT_PATH,

    uninstallScriptPath = environment["UNINSTALL_SCRIPT_PATH"] ?: DEFAULT_UNINSTALL_SCRIPT_PATH,
  )
}
