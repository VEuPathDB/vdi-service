package vdi.module.handler.imports.triggers.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WarningsFile(
  @JsonProperty("warnings")
  val warnings: List<String>
)
