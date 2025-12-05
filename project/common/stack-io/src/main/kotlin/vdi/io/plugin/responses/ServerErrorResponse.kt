package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Defines the response body returned when the plugin HTTP server encounters an
 * unhandled error and returns a 500 error.
 */
data class ServerErrorResponse(
  @field:JsonProperty(Message)
  val message: String
): ErrorResponse {
  companion object JsonKey {
    const val Message = "message"
  }
}