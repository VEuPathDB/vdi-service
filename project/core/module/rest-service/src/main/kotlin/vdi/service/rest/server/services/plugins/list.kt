@file:JvmName("PluginService")
package vdi.service.rest.server.services.plugins

import vdi.model.data.DataType
import vdi.lib.plugin.registry.PluginDetails
import vdi.lib.plugin.registry.PluginRegistry
import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginRegistry.asSequence()

  if (project != null) {
    seq = seq.filter { (_, _, d) -> d.appliesTo(project) }
  }

  return seq.map(::PluginListItem).toList()
}

private fun PluginListItem(p: Triple<DataType, String, PluginDetails>): PluginListItem =
  PluginListItemImpl().also {
    it.displayName = p.third.displayName
    it.projects = p.third.projects
    it.typeName = p.first.toString()
    it.typeVersion = p.second
  }
