package vdi.components.client.plugin_handler

import java.net.URI
import java.net.http.HttpClient
import java.time.Duration

class VDIPluginHandlerClientFactory {
  private val client: HttpClient

  constructor() {
    client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .build()
  }

  constructor(httpClient: HttpClient) {
    client = httpClient
  }

  fun newHandlerClient(handlerServerURI: String): VDIPluginHandlerClient =
    VDIPluginHandlerClientImpl(URI.create(handlerServerURI), client)
}


