package vdi.components.client.plugin_handler.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.client.plugin_handler.FieldName


data class UninstallRequest(
  @JsonProperty(FieldName.VDIID)
  val vdiID: String,

  @JsonProperty(FieldName.ProjectID)
  val projectID: String,
)

