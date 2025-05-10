package vdi.lib.config.vdi

data class PluginScriptConfigs(
  val checkCompatibility: PluginScriptConfig?,
  val dataCleaning: PluginScriptConfig?,
  val dataInstall: PluginScriptConfig?,
  val metaUpdate: PluginScriptConfig?,
  val uninstall: PluginScriptConfig?,
)
