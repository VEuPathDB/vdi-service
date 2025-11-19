package vdi.core.plugin.client.response

import vdi.io.plugin.responses.ScriptErrorResponse

data class ScriptErrorResponse(override val body: ScriptErrorResponse): ServiceErrorResponse {
  override val message
    get() = body.lastMessage

  inline val exitCode
    get() = body.exitCode
}