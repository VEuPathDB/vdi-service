package vdi.core.plugin.client.response

import java.io.InputStream
import vdi.io.plugin.responses.PluginResponseStatus

class StreamResponse(
  override val status: PluginResponseStatus,
  override val body: InputStream,
): PluginResponse