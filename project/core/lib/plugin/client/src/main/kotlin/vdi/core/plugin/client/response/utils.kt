package vdi.core.plugin.client.response

import vdi.io.plugin.responses.PluginResponseStatus

inline val PluginResponse.isSuccessResponse
  get() = status == PluginResponseStatus.Success