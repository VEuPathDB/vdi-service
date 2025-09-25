package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.parse.fields.PartialHostAddress

data class PluginConfig(
  val server: PartialHostAddress,
  val customPath: String?,
  val installRoot: String?,
  val scripts: PluginScriptConfigSet?,

  val dataTypes: Set<DataTypeConfig>,
  val typeChangesEnabled: Boolean?,
  @param:JsonProperty("projectIds")
  @field:JsonProperty("projectIds")
  val projectIDs: Set<String>?,
)
