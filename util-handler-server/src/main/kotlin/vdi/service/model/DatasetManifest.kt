package vdi.service.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetManifest(
  @JsonProperty("inputFiles")
  val inputFiles: Collection<String>,

  @JsonProperty("dataFiles")
  val dataFiles: Collection<String>,
)
