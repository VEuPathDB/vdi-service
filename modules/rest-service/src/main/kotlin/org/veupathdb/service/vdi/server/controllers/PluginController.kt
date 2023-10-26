package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated
import org.veupathdb.service.vdi.generated.resources.VdiPlugins
import org.veupathdb.service.vdi.service.plugins.listPlugins

@Authenticated
class PluginController : VdiPlugins {
  override fun getVdiPlugins(project: String?): VdiPlugins.GetVdiPluginsResponse {
    return VdiPlugins.GetVdiPluginsResponse.respond200WithApplicationJson(listPlugins(project))
  }
}