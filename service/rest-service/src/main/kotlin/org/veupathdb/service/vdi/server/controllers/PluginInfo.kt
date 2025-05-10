package org.veupathdb.service.vdi.server.controllers

import org.veupathdb.service.vdi.generated.resources.VdiPlugins
import org.veupathdb.service.vdi.service.plugins.listPlugins

class PluginInfo : VdiPlugins {
  override fun getVdiPlugins(project: String?): VdiPlugins.GetVdiPluginsResponse {
    return VdiPlugins.GetVdiPluginsResponse.respond200WithApplicationJson(listPlugins(project))
  }
}
