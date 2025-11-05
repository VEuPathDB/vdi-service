package vdi.io.plugin.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class ScriptErrorResponse(
  @get:JsonProperty(ExitCode)
  @param:JsonProperty(ExitCode)
  val exitCode: UByte,

  @get:JsonProperty(LastMessage)
  @param:JsonProperty(LastMessage)
  val lastMessage: String,
) {
  companion object JsonKey {
    const val ExitCode = "exit-code"
    const val LastMessage = "last-message"
  }
}