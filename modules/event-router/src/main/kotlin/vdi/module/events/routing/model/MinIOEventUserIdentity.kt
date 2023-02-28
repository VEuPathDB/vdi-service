package vdi.module.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventUserIdentity(
  @JsonProperty("principalId")
  val principalID: String,
)