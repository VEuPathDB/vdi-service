package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.lib.config.common.HostAddress

data class PluginConfig(
  val server: HostAddress,
  val displayName: String,
  val dataTypes: Set<DataTypeConfig>,
  val typeChangesEnabled: Boolean?,
  val customPath: String?,
  val installRoot: String?,
  @param:JsonProperty("projectIds")
  @field:JsonProperty("projectIds")
  val projectIDs: Set<String>?,
  val scripts: PluginScriptConfigs?,
)
