package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class ScriptErrorResponse(
  @get:JsonProperty(Script)
  @param:JsonProperty(Script)
  val script: String,

  @get:JsonProperty(ExitCode)
  @param:JsonProperty(ExitCode)
  val exitCode: UByte,

  @get:JsonProperty(Message)
  @param:JsonProperty(Message)
  val message: String,
): ErrorResponse {
  companion object JsonKey {
    const val Script = "script"
    const val ExitCode = "exit-code"
    const val Message = "message"
  }
}