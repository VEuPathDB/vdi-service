package vdi.core.plugin.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import vdi.model.field.HostAddress

data class PluginHandlerClientConfig(
  val address: HostAddress,

  val client: HttpClient = HttpClient(Java),
)
