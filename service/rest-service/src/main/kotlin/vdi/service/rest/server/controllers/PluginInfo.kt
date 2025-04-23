package vdi.service.rest.server.controllers

import vdi.service.rest.generated.resources.Plugins
import vdi.service.rest.generated.resources.VdiPlugins
import vdi.service.server.services.plugins.listPlugins

class PluginInfo
  : vdi.service.rest.generated.resources.Plugins
  , vdi.service.rest.generated.resources.VdiPlugins // DEPRECATED API
{
  override fun getPlugins(project: String?) =
    vdi.service.rest.generated.resources.Plugins.GetPluginsResponse.respond200WithApplicationJson(listPlugins(project))!!

  // DEPRECATED API
  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getPlugins(project)"))
  override fun getVdiPlugins(project: String?) =
    vdi.service.rest.generated.resources.VdiPlugins.GetVdiPluginsResponse(getPlugins(project))
}
