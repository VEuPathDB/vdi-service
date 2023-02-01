package vdi.service.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WarningsList(
  @JsonProperty("messages")
  val messages: Collection<String>,
)
