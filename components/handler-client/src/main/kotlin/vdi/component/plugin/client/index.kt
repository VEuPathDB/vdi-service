package vdi.component.plugin.client

fun PluginHandlerClient(config: vdi.component.plugin.client.PluginHandlerClientConfig): vdi.component.plugin.client.PluginHandlerClient {
  return vdi.component.plugin.client.PluginHandlerClientImpl(config)
}