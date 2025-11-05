package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ScriptErrorResponse

@JvmInline
value class ScriptErrorResponse(override val body: ScriptErrorResponse): PluginResponse {
  override val status get() = PluginResponseStatus.ScriptError

  inline val exitCode get() = body.exitCode
  inline val message get() = body.lastMessage
}