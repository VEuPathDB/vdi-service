package vdi.service.plugin.conf

import kotlin.io.path.Path
import vdi.config.raw.vdi.PluginConfig
import vdi.service.plugin.consts.ConfigDefault
import vdi.service.plugin.process.install.data.CheckCompatibilityScript
import vdi.service.plugin.process.install.data.InstallDataScript
import vdi.service.plugin.process.install.meta.InstallMetaScript
import vdi.service.plugin.process.preprocess.ImportScript
import vdi.service.plugin.process.uninstall.UninstallScript

/**
 * Service Specific Configuration Options
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since 1.0.0
 */
internal class ServiceConfiguration(
  val name: String,

  val importScript: ImportScript,
  val installDataScript: InstallDataScript,
  val installMetaScript: InstallMetaScript,
  val uninstallScript: UninstallScript,
  val checkCompatScript: CheckCompatibilityScript,

  val customPath: String,
) {
  constructor(name: String, raw: PluginConfig): this(
    name = name,

    importScript = raw.scripts?.dataCleaning.let {
      ImportScript(
        Path(it?.pathOverride ?: ConfigDefault.ImportScriptPath),
        it?.maxDuration ?: ConfigDefault.ImportScriptMaxDuration,
      )
    },
    installDataScript = raw.scripts?.dataInstall.let {
      InstallDataScript(
        Path(it?.pathOverride ?: ConfigDefault.DataInstallScriptPath),
        it?.maxDuration ?: ConfigDefault.DataInstallScriptMaxDuration,
      )
    },
    installMetaScript = raw.scripts?.metaUpdate.let {
      InstallMetaScript(
        Path(it?.pathOverride ?: ConfigDefault.MetaInstallScriptPath),
        it?.maxDuration ?: ConfigDefault.MetaInstallScriptMaxDuration,
      )
    },
    uninstallScript = raw.scripts?.uninstall.let {
      UninstallScript(
        Path(it?.pathOverride ?: ConfigDefault.UninstallScriptPath),
        it?.maxDuration ?: ConfigDefault.UninstallScriptMaxDuration
      )
    },
    checkCompatScript = raw.scripts?.checkCompatibility.let {
      CheckCompatibilityScript(
        Path(it?.pathOverride ?: ConfigDefault.CheckCompatScriptPath),
        it?.maxDuration ?: ConfigDefault.CheckCompatScriptMaxDuration
      )
    },

    customPath = raw.customPath ?: ConfigDefault.CustomPath,
  )
}
