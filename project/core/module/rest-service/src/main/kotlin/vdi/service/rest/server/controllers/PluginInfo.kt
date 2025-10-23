package vdi.service.rest.server.controllers

import jakarta.ws.rs.core.Context
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.resources.Plugins
import vdi.service.rest.server.services.plugins.listPlugins

class PluginInfo(@param:Context private val uploadConfig: UploadConfig): Plugins {
  override fun getPlugins(project: String?) =
    Plugins.GetPluginsResponse.respond200WithApplicationJson(listPlugins(project, uploadConfig))!!
}
