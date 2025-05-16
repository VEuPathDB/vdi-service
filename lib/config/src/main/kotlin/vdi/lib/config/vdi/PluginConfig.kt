package vdi.lib.config.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.config.PartialHostAddress
import org.veupathdb.vdi.lib.config.PluginConfig
import org.veupathdb.vdi.lib.config.PluginScriptConfigs

data class PluginConfig(
  val server: PartialHostAddress,
  val displayName: String,
  override val customPath: String?,
  override val installRoot: String?,
  override val scripts: PluginScriptConfigs?,

  val dataTypes: Set<DataTypeConfig>,
  val typeChangesEnabled: Boolean?,
  @param:JsonProperty("projectIds")
  @field:JsonProperty("projectIds")
  val projectIDs: Set<String>?,
): PluginConfig
