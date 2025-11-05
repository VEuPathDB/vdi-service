package vdi.core.plugin.client

import vdi.io.plugin.responses.PluginResponseStatus

interface PluginHandlerClientResponse {
  val responseStatus: PluginResponseStatus
}
