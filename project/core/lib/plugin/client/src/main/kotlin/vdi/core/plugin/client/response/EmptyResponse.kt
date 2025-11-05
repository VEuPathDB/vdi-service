package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus

data class EmptyResponse(
  override val status: PluginResponseStatus,
  override val body: Unit = Unit,
): PluginResponse