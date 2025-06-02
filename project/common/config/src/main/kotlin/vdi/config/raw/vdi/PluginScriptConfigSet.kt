package vdi.config.raw.vdi

data class PluginScriptConfigSet(
  val checkCompatibility: PluginScriptConfig?,
  val dataCleaning: PluginScriptConfig?,
  val dataInstall: PluginScriptConfig?,
  val metaUpdate: PluginScriptConfig?,
  val uninstall: PluginScriptConfig?,
)
