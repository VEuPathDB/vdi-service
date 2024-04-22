package org.veupathdb.service.vdi.service.plugins

import org.veupathdb.service.vdi.generated.model.PluginListItem
import org.veupathdb.service.vdi.generated.model.PluginListItemImpl
import vdi.component.plugin.mapping.PluginHandler
import vdi.component.plugin.mapping.PluginHandlers

internal fun listPlugins(project: String?): List<PluginListItem> {
  var seq = PluginHandlers.sequence()

  if (project != null) {
    seq = seq.filter { (_, v) -> v.appliesToProject(project) }
  }

  return seq.map(::PluginListItem).toList()
}

private fun PluginListItem(p: Pair<PluginHandlers.NameVersionPair, PluginHandler>): PluginListItem =
  PluginListItemImpl().also {
    it.displayName = p.second.displayName
    it.projects = p.second.projects()
    it.typeName = p.first.name
    it.typeVersion = p.first.version
  }