package vdi.service.rest.server.controllers

import vdi.service.rest.generated.resources.Plugins
import vdi.service.rest.server.services.plugins.listPlugins

class PluginInfo: Plugins {
  override fun getPlugins(project: String?) =
    Plugins.GetPluginsResponse.respond200WithApplicationJson(listPlugins(project))!!
}
