package vdi.conf

import vdi.Consts
import kotlin.time.Duration

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
  val installDataScriptMaxSeconds: Long,

  val installMetaScriptPath: String,
  val installMetaScriptMaxSeconds: Long,

  val uninstallScriptPath: String,
  val uninstallScriptMaxSeconds: Long,
) {
  constructor(env: Map<String, String> = System.getenv()) : this(
    env[Consts.ENV_IMPORT_SCRIPT_PATH] ?: Consts.DEFAULT_IMPORT_SCRIPT_PATH,
    (env[Consts.ENV_IMPORT_SCRIPT_MAX_DURATION] ?: Consts.DEFAULT_IMPORT_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Consts.ENV_INSTALL_DATA_SCRIPT_PATH] ?: Consts.DEFAULT_DATA_INSTALL_SCRIPT_PATH,
    (env[Consts.ENV_INSTALL_DATA_SCRIPT_MAX_DURATION] ?: Consts.DEFAULT_INSTALL_DATA_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Consts.ENV_INSTALL_META_SCRIPT_PATH] ?: Consts.DEFAULT_META_INSTALL_SCRIPT_PATH,
    (env[Consts.ENV_INSTALL_META_SCRIPT_MAX_DURATION] ?: Consts.DEFAULT_INSTALL_META_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Consts.ENV_UNINSTALL_SCRIPT_PATH] ?: Consts.DEFAULT_UNINSTALL_SCRIPT_PATH,
    (env[Consts.ENV_UNINSTALL_SCRIPT_MAX_DURATION] ?: Consts.DEFAULT_UNINSTALL_SCRIPT_MAX_DURATION).toDurSeconds()
  )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.toDurSeconds() = Duration.parse(this).inWholeSeconds
