package vdi.daemon.events.routing.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MinIOEventUserIdentity(
  @param:JsonProperty("principalId")
  @field:JsonProperty("principalId")
  val principalID: String,
)