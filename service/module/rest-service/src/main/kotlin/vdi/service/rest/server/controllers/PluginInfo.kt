package vdi.service.rest.server.controllers

import vdi.service.rest.generated.resources.Plugins
import vdi.service.rest.generated.resources.VdiPlugins
import vdi.service.rest.server.services.plugins.listPlugins

class PluginInfo
  : Plugins
  , VdiPlugins // DEPRECATED API
{
  override fun getPlugins(project: String?) =
    Plugins.GetPluginsResponse.respond200WithApplicationJson(listPlugins(project))!!

  // DEPRECATED API
  @Deprecated("to be removed with API refactor", replaceWith = ReplaceWith("getPlugins(project)"))
  override fun getVdiPlugins(project: String?) =
    VdiPlugins.GetVdiPluginsResponse(getPlugins(project))
}
