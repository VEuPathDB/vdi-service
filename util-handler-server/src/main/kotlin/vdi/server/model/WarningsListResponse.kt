package vdi.server.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WarningsListResponse(
  @JsonProperty("messages")
  val messages: Collection<String>,
)
