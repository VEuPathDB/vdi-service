package vdi.core.plugin.client

fun PluginHandlerClient(name: String, config: PluginHandlerClientConfig): PluginHandlerClient {
  return PluginHandlerClientImpl(name, config)
}
