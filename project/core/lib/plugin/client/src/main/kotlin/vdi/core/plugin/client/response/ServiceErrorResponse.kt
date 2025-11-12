package vdi.core.plugin.client.response

import vdi.io.plugin.responses.ScriptErrorResponse
import vdi.io.plugin.responses.ServerErrorResponse

sealed interface ServiceErrorResponse: PluginResponse {
  val message: String
}

data class ScriptErrorResponse(override val body: ScriptErrorResponse): ServiceErrorResponse {
  override val message
    get() = body.lastMessage

  inline val exitCode
    get() = body.exitCode
}

data class ServerErrorResponse(override val body: ServerErrorResponse): ServiceErrorResponse {
  override val message
    get() = body.message
}
