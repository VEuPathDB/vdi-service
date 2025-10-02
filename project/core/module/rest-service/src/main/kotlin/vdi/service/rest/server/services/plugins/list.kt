@file:JvmName("PluginService")
package vdi.service.rest.server.services.plugins

import vdi.core.plugin.registry.PluginDetails
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetType
import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginRegistry.asSequence()

  if (project != null) {
    seq = seq.filter { (_, d) -> d.appliesTo(project) }
  }

  return seq.map(::PluginListItem).toList()
}

private fun PluginListItem(p: Pair<DatasetType, PluginDetails>): PluginListItem =
  PluginListItemImpl().also {
    it.pluginName      = p.second.name
    it.installTargets  = p.second.projects
    it.typeDisplayName = PluginRegistry.categoryFor(p.first)
    it.typeName        = p.first.name.toString()
    it.typeVersion     = p.first.version
  }
