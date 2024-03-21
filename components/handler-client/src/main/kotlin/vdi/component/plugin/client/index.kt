package vdi.component.plugin.client

fun PluginHandlerClient(config: PluginHandlerClientConfig): PluginHandlerClient {
  return PluginHandlerClientImpl(config)
}