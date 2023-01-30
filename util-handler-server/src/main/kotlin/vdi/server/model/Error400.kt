package vdi.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.util.JSON

data class Error400(
  @JsonProperty("message")
  val message: String
)

fun make400JSONString(message: String): String =
  JSON.writeValueAsString(Error400(message))