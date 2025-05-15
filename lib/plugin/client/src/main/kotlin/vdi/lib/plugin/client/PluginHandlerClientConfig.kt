package vdi.lib.plugin.client

import org.veupathdb.vdi.lib.common.util.HostAddress
import java.net.http.HttpClient

data class PluginHandlerClientConfig(
  val address: HostAddress,

  val client: HttpClient = HttpClient.newHttpClient()
)
