package org.veupathdb.vdi.lib.handler.client

fun PluginHandlerClient(config: PluginHandlerClientConfig): PluginHandlerClient {
  return PluginHandlerClientImpl(config)
}