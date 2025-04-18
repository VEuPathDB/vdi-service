package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.service.vdi.generated.resources.Plugins
import org.veupathdb.service.vdi.generated.resources.VdiPlugins
import org.veupathdb.service.vdi.server.services.plugins.listPlugins

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
