package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus

sealed interface PluginResponse {
  val status: PluginResponseStatus
  val body: Any
}

