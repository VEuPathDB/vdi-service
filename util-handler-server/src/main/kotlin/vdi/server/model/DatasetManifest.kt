package vdi.server.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DatasetManifest(
  @JsonProperty("dataFiles")
  var dataFiles: List<String>
)