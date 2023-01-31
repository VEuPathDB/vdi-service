package vdi.server.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.util.JSON

data class SimpleErrorResponse(
  @JsonProperty("message")
  val message: String
)

fun makeSimpleErrorJSONString(message: String): String =
  JSON.writeValueAsString(SimpleErrorResponse(message))
