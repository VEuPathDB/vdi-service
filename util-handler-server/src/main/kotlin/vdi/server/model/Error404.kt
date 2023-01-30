package vdi.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.util.JSON

data class Error404(
  @JsonProperty("message")
  val message: String
)

fun make404JSONString(message: String): String =
  JSON.writeValueAsString(Error404(message))