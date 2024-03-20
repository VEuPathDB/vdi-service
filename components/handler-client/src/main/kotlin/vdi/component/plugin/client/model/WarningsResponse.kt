package vdi.component.plugin.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WarningsResponse(
  @JsonProperty(vdi.component.plugin.client.FieldName.Warnings)
  val warnings: List<String>
)
