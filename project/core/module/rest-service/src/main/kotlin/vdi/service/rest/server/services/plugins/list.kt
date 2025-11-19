@file:JvmName("PluginService")

package vdi.service.rest.server.services.plugins

import vdi.core.plugin.registry.PluginDetails
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.meta.DatasetType
import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl
import vdi.service.rest.server.outputs.DatasetTypeOutput

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginRegistry.asSequence()

  if (project != null) {
    seq = seq.filter { (_, d) -> d.appliesTo(project) }
  }

  return seq.map(::PluginListItem).toList()
}

private fun PluginListItem(p: Pair<DatasetType, PluginDetails>): PluginListItem =
  PluginListItemImpl().also {
    val config = PluginRegistry.configDataFor(p.first)

    it.pluginName = p.second.name
    it.installTargets = p.second.projects
    it.type = DatasetTypeOutput(p.first)
    it.maxFileSize = config.maxFileSize.toLong()
    it.allowedFileExtensions = config.allowedFileExtensions.asList()
  }
