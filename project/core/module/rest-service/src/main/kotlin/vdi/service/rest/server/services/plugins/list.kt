@file:JvmName("PluginService")
package vdi.service.rest.server.services.plugins

import kotlin.math.min
import vdi.core.plugin.registry.PluginDetails
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetType
import vdi.service.rest.config.UploadConfig
import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl

internal fun listPlugins(
  project: String?,
  uploadConfig: UploadConfig,
): List<PluginListItem> {
  var seq = PluginRegistry.asSequence()

  if (project != null) {
    seq = seq.filter { (_, d) -> d.appliesTo(project) }
  }

  return seq.map {
    PluginListItem(it, uploadConfig.maxUploadSize)
  }.toList()
}

private fun PluginListItem(p: Pair<DatasetType, PluginDetails>, svcMaxFileSize: ULong): PluginListItem =
  PluginListItemImpl().also {
    it.pluginName      = p.second.name
    it.installTargets  = p.second.projects
    it.typeDisplayName = PluginRegistry.categoryFor(p.first)
    it.typeName        = p.first.name.toString()
    it.typeVersion     = p.first.version
    it.maxFileSize     = min(PluginRegistry.maxFileSizeFor(p.first), svcMaxFileSize).toLong()
  }
