package org.veupathdb.vdi.lib.handler.client

import org.veupathdb.vdi.lib.common.util.HostAddress
import java.net.http.HttpClient

data class PluginHandlerClientConfig(
  val address: HostAddress,

  val client: HttpClient = HttpClient.newBuilder().build()
)