package vdi.core.plugin.client.response

import vdi.io.plugin.responses.ServerErrorResponse

data class ServerErrorResponse(override val body: ServerErrorResponse): ServiceErrorResponse {
  override val message
    get() = body.message
}