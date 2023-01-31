package vdi.conf

import vdi.Const
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
    env[Const.EnvKey.IMPORT_SCRIPT_PATH] ?: Const.ConfigDefault.IMPORT_SCRIPT_PATH,
    (env[Const.EnvKey.IMPORT_SCRIPT_MAX_DURATION] ?: Const.ConfigDefault.IMPORT_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Const.EnvKey.INSTALL_DATA_SCRIPT_PATH] ?: Const.ConfigDefault.DATA_INSTALL_SCRIPT_PATH,
    (env[Const.EnvKey.INSTALL_DATA_SCRIPT_MAX_DURATION] ?: Const.ConfigDefault.INSTALL_DATA_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Const.EnvKey.INSTALL_META_SCRIPT_PATH] ?: Const.ConfigDefault.META_INSTALL_SCRIPT_PATH,
    (env[Const.EnvKey.INSTALL_META_SCRIPT_MAX_DURATION] ?: Const.ConfigDefault.INSTALL_META_SCRIPT_MAX_DURATION).toDurSeconds(),
    env[Const.EnvKey.UNINSTALL_SCRIPT_PATH] ?: Const.ConfigDefault.UNINSTALL_SCRIPT_PATH,
    (env[Const.EnvKey.UNINSTALL_SCRIPT_MAX_DURATION] ?: Const.ConfigDefault.UNINSTALL_SCRIPT_MAX_DURATION).toDurSeconds()
  )
}

@Suppress("NOTHING_TO_INLINE")
private inline fun String.toDurSeconds() = Duration.parse(this).inWholeSeconds
