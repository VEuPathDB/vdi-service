package vdi.lane.imports.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WarningsFile(
  @JsonProperty("warnings")
  val warnings: List<String>
)
