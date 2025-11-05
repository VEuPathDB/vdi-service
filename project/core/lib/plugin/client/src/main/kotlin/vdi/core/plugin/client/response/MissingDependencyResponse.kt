package vdi.core.plugin.client.response

import vdi.io.plugin.responses.MissingDependencyResponse
import vdi.io.plugin.responses.PluginResponseStatus

@JvmInline
value class MissingDependencyResponse(override val body: MissingDependencyResponse): PluginResponse {
  override val status: PluginResponseStatus
    get() = PluginResponseStatus.MissingDependencyError
}