package vdi.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.util.JSON

data class Error500(
  @JsonProperty("message")
  val message: String
)

fun make500JSONString(message: String): String =
  JSON.writeValueAsString(Error500(message))