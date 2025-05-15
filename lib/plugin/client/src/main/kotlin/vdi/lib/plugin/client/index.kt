package vdi.lib.plugin.client

fun PluginHandlerClient(config: PluginHandlerClientConfig): PluginHandlerClient {
  return PluginHandlerClientImpl(config)
}
