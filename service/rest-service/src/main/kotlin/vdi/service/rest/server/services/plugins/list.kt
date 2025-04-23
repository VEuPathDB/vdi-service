package vdi.service.rest.server.services.plugins

import vdi.service.rest.generated.model.PluginListItem
import vdi.service.rest.generated.model.PluginListItemImpl
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginHandlers.sequence()

  if (project != null) {
    seq = seq.filter { (_, v) -> v.appliesToProject(project) }
  }

  return seq.map(::PluginListItem).toList()
}

private fun PluginListItem(p: Pair<PluginHandlers.NameVersionPair, PluginHandler>): PluginListItem =
  vdi.service.rest.generated.model.PluginListItemImpl().also {
    it.displayName = p.second.displayName
    it.projects = p.second.projects()
    it.typeName = p.first.name.toString()
    it.typeVersion = p.first.version
  }
