package vdi.config.raw.vdi

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.config.parse.fields.PartialHostAddress

data class PluginConfig(
  val server: PartialHostAddress,

  val dataTypes: Set<DataTypeConfig>,

  val typeChangesEnabled: Boolean = false,

  val customPath: String?,

  val installRoot: String?,

  @param:JsonProperty(KeyProjectIDs)
  @field:JsonProperty(KeyProjectIDs)
  val projectIDs: Set<String>?,

  val scripts: PluginScriptConfigSet?,
) {
  private companion object {
    const val KeyProjectIDs = "projectIds"
  }
}