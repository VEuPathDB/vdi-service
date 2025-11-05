package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus
import vdi.io.plugin.responses.ServerErrorResponse

@JvmInline
value class ServerErrorResponse(override val body: ServerErrorResponse): PluginResponse {
  override val status get() = PluginResponseStatus.ServerError

  inline val message get() = body.message
}