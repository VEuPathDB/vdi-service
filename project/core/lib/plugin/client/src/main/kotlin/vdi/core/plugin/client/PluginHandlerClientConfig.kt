package vdi.core.plugin.client

import java.net.http.HttpClient
import vdi.model.field.HostAddress

data class PluginHandlerClientConfig(
  val address: HostAddress,

  val client: HttpClient = HttpClient.newHttpClient()
)
