package vdi.service.rest.server.controllers

import vdi.service.rest.generated.resources.VdiPlugins
import vdi.service.rest.server.services.plugins.listPlugins

@Deprecated("to be removed after client update")
class DeprecatedPluginInfo: VdiPlugins {
  override fun getVdiPlugins(project: String?) =
    VdiPlugins.GetVdiPluginsResponse.respond200WithApplicationJson(listPlugins(project))!!
}
