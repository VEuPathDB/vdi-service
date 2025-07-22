package vdi.model.api.internal

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Defines a common response body consisting of a JSON object containing a
 * single key "message" mapped to a string value containing an error message.
 */
data class SimpleErrorResponse(@field:JsonProperty(Message) val message: String) {
  companion object JsonKey {
    const val Message = "message"
  }
}