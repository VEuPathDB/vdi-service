package vdi.service.server.controllers

import vdi.service.generated.resources.Plugins
import vdi.service.generated.resources.VdiPlugins
import vdi.service.server.services.plugins.listPlugins

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
