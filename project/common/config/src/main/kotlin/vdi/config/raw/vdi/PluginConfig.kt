package vdi.config.raw.vdi

import vdi.config.parse.fields.PartialHostAddress

data class PluginConfig(
  val server: PartialHostAddress,

  val dataTypes: Set<DataTypeConfig>,

  val customPath: String?,

  val installRoot: String?,

  val scripts: PluginScriptConfigSet?,
)