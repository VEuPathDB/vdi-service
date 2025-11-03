package vdi.core.plugin.client

fun PluginHandlerClient(config: PluginHandlerClientConfig): PluginHandlerClient {
  return PluginHandlerClientImpl(config)
}
