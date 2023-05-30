package org.veupathdb.vdi.lib.handler.client

import org.veupathdb.vdi.lib.common.util.HostAddress
import java.net.http.HttpClient
import java.time.Duration

data class PluginHandlerClientConfig(
  val address: HostAddress,

  val client: HttpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofMinutes(2))
    .build()
)